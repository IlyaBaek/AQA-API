import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUsersTests {
    private static final Random random = new Random();

    @Test
    public void createUserTest() {
        ZipCodesClient.createZipCodeIfNotExist();
        ResponseWrapper getZipCodesResponseBeforeCreation = ZipCodesClient.getZipCodes();
        List<String> zipCodesListBeforeCreation = (List<String>) getZipCodesResponseBeforeCreation.getResponseBody();
        String exactZipCode = zipCodesListBeforeCreation.get(random.nextInt(zipCodesListBeforeCreation.size()));
        UsersDto userToAdd = new UsersDto();
        userToAdd.setZipCode(exactZipCode);

        ResponseWrapper createUserResponse = UsersClient.createUser(userToAdd);

        ResponseWrapper getZipCodesResponseAfterCreation = ZipCodesClient.getZipCodes();
        List<String> zipCodesListAfterCreation = (List<String>) getZipCodesResponseAfterCreation.getResponseBody();
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        UsersDto addedUser = usersList.get(usersList.indexOf(userToAdd));
        assertAll("Status code is 201 // user is added, zip code is removed",
                () -> assertFalse(zipCodesListAfterCreation.contains(exactZipCode), "zipCode should be removed"),
                () -> assertTrue(usersList.contains(userToAdd), "user should be added"),
                () -> assertEquals(userToAdd, addedUser, "all user fields are as expected"),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithRequiredFieldsTest() {
        UsersDto userToAdd = new UsersDto();
        userToAdd.setAge(null);

        ResponseWrapper createUserResponse = UsersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        UsersDto addedUser = usersList.get(usersList.indexOf(userToAdd));
        assertAll("Status code is 201 // user is added",
                () -> assertTrue(usersList.contains(userToAdd)),
                () -> assertEquals(userToAdd, addedUser),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithIncorrectZipCodeTest() {
        UsersDto userToAdd = new UsersDto();
        userToAdd.setZipCode(ZipCodesClient.getRandomNotExistingZipCode());

        ResponseWrapper createUserResponse = UsersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        assertAll("Status code is 424 // user is NOT added",
                () -> assertFalse(usersList.contains(userToAdd)),
                () -> assertEquals(424, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithDuplicateNameAndSexTest() {
        ResponseWrapper getUsersBeforeAddingDuplicateResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersListBeforeAddingDuplicate = (List<UsersDto>) getUsersBeforeAddingDuplicateResponse.getResponseBody();
        UsersDto userWithDuplicateNameAndSex = usersListBeforeAddingDuplicate.get(random.nextInt(usersListBeforeAddingDuplicate.size()));
        UsersDto userToAdd = new UsersDto(userWithDuplicateNameAndSex.getName(), userWithDuplicateNameAndSex.getUserSex());

        ResponseWrapper createUserResponse = UsersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponseAfterAddingDuplicate = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersListAfterAddingDuplicate = (List<UsersDto>) getUsersResponseAfterAddingDuplicate.getResponseBody();
        assertAll("Status code is 400 // user is NOT added",
                () -> assertEquals(usersListBeforeAddingDuplicate, usersListAfterAddingDuplicate),
                () -> assertEquals(400, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithoutRequiredField() {
        UsersDto userToAdd = new UsersDto(null, null);

        ResponseWrapper createUserResponse = UsersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        assertAll("Status code is 409 // user is NOT added",
                () -> assertFalse(usersList.contains(userToAdd)),
                () -> assertEquals(409, createUserResponse.getResponseCode()));
    }
}
