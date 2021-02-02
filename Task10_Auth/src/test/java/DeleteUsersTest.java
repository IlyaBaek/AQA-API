import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteUsersTest {
    Random random = new Random();

    @BeforeEach
    public void precondition() {
        UsersClient.createUserIfNotExist();
    }

    @Test
    public void deleteUserTest() {
        List<UsersDto> usersList = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        UsersDto usersDto = usersList.get(random.nextInt(usersList.size()));

        ResponseWrapper deleteUserResponse = UsersClient.deleteUser(usersDto);

        List<UsersDto> userListAfterDelete = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        List<String> zipCodesAfterDelete = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        assertAll("204, user removed, zip code is back",
                () -> assertEquals(204, deleteUserResponse.getResponseCode()),
                () -> assertFalse(userListAfterDelete.contains(usersDto), "removed user doesn't exist in users list"),
                () -> assertTrue(zipCodesAfterDelete.contains(usersDto.getZipCode()), "removed user zip code added to the zip codes list")
        );
    }

    @Test
    public void deleteUserByRequiredFieldsTest() {
        List<UsersDto> usersList = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        UsersDto usersDto = usersList.get(random.nextInt(usersList.size()));
        String zipCode = usersDto.getZipCode();
        Integer age = usersDto.getAge();
        usersDto.setZipCode(null);
        usersDto.setAge(null);

        ResponseWrapper responseWrapper = UsersClient.deleteUser(usersDto);

        List<UsersDto> userListAfterDelete = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        List<String> zipCodesAfterDelete = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        usersDto.setZipCode(zipCode);
        usersDto.setAge(age);
        assertAll("204, user removed, zip code is back",
                () -> assertEquals(204, responseWrapper.getResponseCode()),
                () -> assertFalse(userListAfterDelete.contains(usersDto), "removed user doesn't exist in users list"),
                () -> assertTrue(zipCodesAfterDelete.contains(usersDto.getZipCode()), "removed user zip code added to the zip codes list")
        );
    }

    @Test
    public void deleteUserWithMissedRequiredFieldTest() {
        List<UsersDto> usersList = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        UsersDto usersDto = usersList.get(random.nextInt(usersList.size()));
        String name = usersDto.getName();
        UsersDto.Sex sex = usersDto.getUserSex();
        //usersDto.setName(null);
        usersDto.setUserSex(null);

        ResponseWrapper responseWrapper = UsersClient.deleteUser(usersDto);

        List<UsersDto> userListAfterDelete = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        usersDto.setName(name);
        usersDto.setUserSex(sex);
        assertAll("409, user is NOT removed",
                () -> assertEquals(409, responseWrapper.getResponseCode()),
                () -> assertTrue(userListAfterDelete.contains(usersDto), "User wasn't removed and exist in users list")
        );
    }
}
