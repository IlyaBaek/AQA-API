import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUsersTests {
    private ZipCodesClient zipCodesClient = new ZipCodesClient();
    private UsersClient usersClient = new UsersClient();
    private static final Random random = new Random();

    @Test
    public void createUserTest() {
        ZipCodesClient.createZipCodeIfNotExist(zipCodesClient);
        ResponseWrapper getZipCodesResponseBeforeCreation = zipCodesClient.getZipCodes();
        int zipCodesSize = getZipCodesResponseBeforeCreation.getResponseBody().size();
        List<String> zipCodesListBeforeCreation = new ArrayList<>(getZipCodesResponseBeforeCreation.getResponseBody());
        String exactZipCode = zipCodesListBeforeCreation.get(random.nextInt(zipCodesSize));
        UsersDto userToAdd = new UsersDto();
        userToAdd.setZipCode(exactZipCode);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getZipCodesResponseAfterCreation = zipCodesClient.getZipCodes();
        List<String> zipCodesListAfterCreation = new ArrayList<>(getZipCodesResponseAfterCreation.getResponseBody());
        ResponseWrapper getUsersResponse = usersClient.getUsers();
        List<UsersDto> usersList = new ArrayList<>(getUsersResponse.getResponseBody());
        UsersDto addedUser = usersList.get(usersList.indexOf(userToAdd));
        assertAll("Status code is 201 // user is added, zip code is removed",
                () -> assertFalse(zipCodesListAfterCreation.contains(exactZipCode)),
                () -> assertTrue(usersList.contains(userToAdd)),
                () -> assertEquals(userToAdd, addedUser),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithRequiredFieldsTest() {
        String name = RandomStringUtils.random(5, true, false);
        UsersDto.Sex userSex = UsersDto.Sex.getRandomSex();
        UsersDto userToAdd = new UsersDto(name, userSex);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = usersClient.getUsers();
        List<UsersDto> usersList = new ArrayList<>(getUsersResponse.getResponseBody());
        UsersDto addedUser = usersList.get(usersList.indexOf(userToAdd));
        assertAll("Status code is 201 // user is added",
                () -> assertTrue(usersList.contains(userToAdd)),
                () -> assertEquals(userToAdd, addedUser),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithIncorrectZipCodeTest() {
        ResponseWrapper getZipCodesResponse = zipCodesClient.getZipCodes();
        String zipCode;
        do {
            zipCode = RandomStringUtils.random(5, false, true);
        } while (getZipCodesResponse.getResponseBody().contains(zipCode));
        UsersDto userToAdd = new UsersDto();
        userToAdd.setZipCode(zipCode);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = usersClient.getUsers();
        List<UsersDto> usersList = new ArrayList<>(getUsersResponse.getResponseBody());
        assertAll("Status code is 424 // user is NOT added",
                () -> assertFalse(usersList.contains(userToAdd)),
                () -> assertEquals(424, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithDuplicateNameAndSexTest() {
        ResponseWrapper getUsersBeforeAddingDuplicateResponse = usersClient.getUsers();
        List<UsersDto> usersListBeforeAddingDuplicate = new ArrayList<>(getUsersBeforeAddingDuplicateResponse.getResponseBody());
        UsersDto userWithDuplicateNameAndSex = usersListBeforeAddingDuplicate.get(random.nextInt(usersListBeforeAddingDuplicate.size()));
        UsersDto userToAdd = new UsersDto(userWithDuplicateNameAndSex.getName(), userWithDuplicateNameAndSex.getUserSex());

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponseAfterAddingDuplicate = usersClient.getUsers();
        List<UsersDto> usersListAfterAddingDuplicate = new ArrayList<>(getUsersResponseAfterAddingDuplicate.getResponseBody());
        assertAll("Status code is 400 // user is NOT added",
                () -> assertEquals(usersListBeforeAddingDuplicate, usersListAfterAddingDuplicate),
                () -> assertEquals(400, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithoutRequiredField() {
        String name = null;
        UsersDto.Sex userSex = null;
        UsersDto userToAdd = new UsersDto(name, userSex);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = usersClient.getUsers();
        List<UsersDto> usersList = new ArrayList<>(getUsersResponse.getResponseBody());
        assertAll("Status code is 409 // user is NOT added",
                () -> assertFalse(usersList.contains(userToAdd)),
                () -> assertEquals(409, createUserResponse.getResponseCode()));
    }
}
