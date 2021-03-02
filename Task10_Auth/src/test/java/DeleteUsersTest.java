import io.qameta.allure.Allure;
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
        Allure.addAttachment("Users list before delete", Mapper.UserDtoToString(usersList));
        Allure.addAttachment("User to delete", Mapper.UserDtoToString(usersDto));
        Allure.addAttachment("Users list after delete", Mapper.UserDtoToString(userListAfterDelete));
        Allure.addAttachment("Zip Codes after delete", zipCodesAfterDelete.toString());
        assertAll("204, user removed, zip code is back",
                () -> assertEquals(204, deleteUserResponse.getResponseCode()),
                () -> assertFalse(userListAfterDelete.contains(usersDto), "removed user exist in users list"),
                () -> assertTrue(zipCodesAfterDelete.contains(usersDto.getZipCode()), "removed user zip code is NOT added to the zip codes list")
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
        Allure.addAttachment("Users list before delete", Mapper.UserDtoToString(usersList));
        Allure.addAttachment("User to delete", Mapper.UserDtoToString(usersDto));
        Allure.addAttachment("Users list after delete", Mapper.UserDtoToString(userListAfterDelete));
        Allure.addAttachment("Zip Codes after delete", zipCodesAfterDelete.toString());
        assertAll("204, user removed, zip code is back",
                () -> assertEquals(204, responseWrapper.getResponseCode()),
                () -> assertFalse(userListAfterDelete.contains(usersDto), "removed user exist in users list"),
                () -> assertTrue(zipCodesAfterDelete.contains(usersDto.getZipCode()), "removed user zip code is NOT added to the zip codes list")
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
        Allure.addAttachment("User to delete", Mapper.UserDtoToString(usersDto));

        ResponseWrapper responseWrapper = UsersClient.deleteUser(usersDto);

        List<UsersDto> userListAfterDelete = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        usersDto.setName(name);
        usersDto.setUserSex(sex);
        Allure.addAttachment("Users list before delete", Mapper.UserDtoToString(usersList));
        Allure.addAttachment("Users list after delete", Mapper.UserDtoToString(userListAfterDelete));
        assertAll("409, user is NOT removed",
                () -> assertEquals(409, responseWrapper.getResponseCode()),
                () -> assertTrue(userListAfterDelete.contains(usersDto), "User removed and doesn't exist in users list")
        );
    }
}
