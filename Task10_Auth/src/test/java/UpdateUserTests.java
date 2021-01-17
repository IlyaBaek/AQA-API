import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UpdateUserDto;
import reseponsesDTO.UsersDto;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateUserTests {
    private static final Random random = new Random();

    @BeforeEach
    public void precondition() {
        UsersClient.createUserIfNotExist();
        ZipCodesClient.createZipCodeIfNotExist();
    }

    @Test
    public void updateUser() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(random.nextInt(3));

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 200 and user is updated",
                () -> assertEquals(200, updateUserResponse.getResponseCode()),
                () -> assertTrue(usersAfterUpdate.contains(updateUserDto.getUserNewValues())));
    }

    @Test
    public void updateUserWithUnavailableZipCode() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(3);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 424 and user is not updated",
                () -> assertEquals(424, updateUserResponse.getResponseCode()),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserNewValues())));
    }

    @Test
    public void updateUserWithoutRequiredFields() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(4);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 409 and user is not updated",
                () -> assertEquals(409, updateUserResponse.getResponseCode()),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserNewValues())));
    }

    public UpdateUserDto getUserToUpdateWithDifferentSetOfFields(int choice) {
        ResponseWrapper getZipCodesResponse = ZipCodesClient.getZipCodes();
        List<String> availableZipCodes = (List<String>) getZipCodesResponse.getResponseBody();
        String updatedZipCode = availableZipCodes.get(random.nextInt(availableZipCodes.size()));

        ResponseWrapper getUsersResponse = UsersClient.getUsers();
        List<UsersDto> currentUsers = (List<UsersDto>) getUsersResponse.getResponseBody();
        UsersDto userToChange = currentUsers.get(random.nextInt(currentUsers.size()));

        UsersDto userNewValues = new UsersDto();
        switch (choice) {
            //All fields are new -> Patch
            case 0:
                userNewValues.setZipCode(updatedZipCode);
                break;
            //Only required fields are new -> Put
            case 1:
                userNewValues.setZipCode(userToChange.getZipCode());
                userNewValues.setAge(userToChange.getAge());
                break;
            //Only non-required fields are new -> Put
            case 2:
                userNewValues.setName(userToChange.getName());
                userNewValues.setAge(userToChange.getAge());
                userNewValues.setZipCode(updatedZipCode);
                break;
            //not existing zip code
            case 3:
                String notExistingZipCode = ZipCodesClient.getRandomNotExistingZipCode();
                userNewValues.setZipCode(notExistingZipCode);
                break;
            //empty required fields
            case 4:
                userNewValues.setZipCode(updatedZipCode);
                userNewValues.setName(null);
                break;
        }
        return new UpdateUserDto(userNewValues, userToChange);
    }
}
