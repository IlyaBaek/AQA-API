import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UploadUsersTest {
    private Random random = new Random();

    @Test
    public void uploadUsersTest() {
        int numberOfUsersToUpload = 4;
        List<String> zipCodesListOfUsersBeforeUpload = new ArrayList<>();
        List<UsersDto> usersToUpload = getUsersListToUpload(numberOfUsersToUpload);
        List<String> zipCodesListOfUsersToUpload = new ArrayList<>();
        for (UsersDto usersDto : usersToUpload) {
            zipCodesListOfUsersToUpload.add(usersDto.getZipCode());
        }

        ResponseWrapper uploadResponse = UsersClient.uploadUsers(getFileWithListOfUsers(usersToUpload));

        ResponseWrapper usersAfterUpload = UsersClient.getUsers();
        List<String> zipCodesAfterUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        int numberOfUsersUploaded = Integer.parseInt(uploadResponse.getResponseBody().toString().replaceAll("[^0-9]", ""));
        assertAll("status code is 201 and users are replaced from users from file",
                () -> assertEquals(201, uploadResponse.getResponseCode()),
                () -> assertEquals(usersAfterUpload.getResponseBody(), usersToUpload),
                () -> assertEquals(numberOfUsersToUpload, numberOfUsersUploaded),
                () -> assertTrue(zipCodesAfterUpload.containsAll(zipCodesListOfUsersBeforeUpload), "At least one of replaced users zip code is not returned to the list of available zip codes"),
                () -> assertTrue(Collections.disjoint(zipCodesAfterUpload, zipCodesListOfUsersToUpload), "Uploaded users zip codes are not removed from the list of available zip codes"));
    }

    @Test
    public void uploadUsersWithUnavailableZipCodeTest() {
        int numberOfUsersToUpload = 3;
        List<UsersDto> userListBeforeUpload = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        List<UsersDto> usersToUpload = getUsersListToUpload(numberOfUsersToUpload);
        usersToUpload.get(random.nextInt(usersToUpload.size())).setZipCode(ZipCodesClient.getRandomNotExistingZipCode());
        List<String> zipCodesBeforeUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();

        ResponseWrapper uploadResponse = UsersClient.uploadUsers(getFileWithListOfUsers(usersToUpload));

        ResponseWrapper usersAfterUpload = UsersClient.getUsers();
        List<String> zipCodesAfterUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        assertAll("status code is 424 and users are not uploaded",
                () -> assertEquals(424, uploadResponse.getResponseCode()),
                () -> assertEquals(userListBeforeUpload, usersAfterUpload.getResponseBody()),
                () -> assertEquals(zipCodesBeforeUpload, zipCodesAfterUpload));
    }

    @Test
    public void uploadUsersWithMissingRequiredFieldsTest() {
        int numberOfUsersToUpload = 3;
        List<UsersDto> userListBeforeUpload = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        List<UsersDto> usersToUpload = getUsersListToUpload(numberOfUsersToUpload);
        usersToUpload.get(random.nextInt(usersToUpload.size())).setName(null);
        usersToUpload.get(random.nextInt(usersToUpload.size())).setUserSex(null);
        List<String> zipCodesBeforeUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();

        ResponseWrapper uploadResponse = UsersClient.uploadUsers(getFileWithListOfUsers(usersToUpload));

        ResponseWrapper usersAfterUploadResponse = UsersClient.getUsers();
        List<String> zipCodesAfterUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        assertAll("status code is 409 and users are not uploaded",
                () -> assertEquals(409, uploadResponse.getResponseCode()),
                () -> assertEquals(userListBeforeUpload, usersAfterUploadResponse.getResponseBody()),
                () -> assertEquals(zipCodesBeforeUpload, zipCodesAfterUpload));
    }

    @Test
    public void uploadUsersWithInvalidFile() {
        List<UsersDto> userListBeforeUpload = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        List<String> zipCodesBeforeUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        File invalidFile = new File("./src/main/resources/UploadUsersFile.json");
        try {
            FileWriter fileWriter = new FileWriter(invalidFile);
            fileWriter.write("not a json");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseWrapper uploadResponse = UsersClient.uploadUsers(invalidFile);

        ResponseWrapper usersAfterUploadResponse = UsersClient.getUsers();
        List<String> zipCodesAfterUpload = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        assertAll("status code is 400 and users are not uploaded",
                () -> assertEquals(400, uploadResponse.getResponseCode()),
                () -> assertEquals(userListBeforeUpload, usersAfterUploadResponse.getResponseBody()),
                () -> assertEquals(zipCodesBeforeUpload, zipCodesAfterUpload));
    }

    private List<UsersDto> getUsersListToUpload(int usersCount) {
        ZipCodesClient.createRandomZipCodes(usersCount);
        List<String> zipCodes = new ArrayList<>(new HashSet<>((List<String>) ZipCodesClient.getZipCodes().getResponseBody()));
        List<UsersDto> usersToUploadList = new ArrayList<>();
        for (int i = 0; i < usersCount; i++) {
            UsersDto usersDto = new UsersDto();
            usersDto.setZipCode(zipCodes.get(i));
            usersToUploadList.add(usersDto);
        }
        return usersToUploadList;
    }

    private File getFileWithListOfUsers(List<UsersDto> usersToUpload) {
        ObjectMapper objectMapper = new ObjectMapper();
        File myFile = new File("./src/main/resources/UploadUsersFile.json");
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(usersToUpload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try (FileOutputStream outputStream = new FileOutputStream(myFile)) {
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myFile;
    }
}
