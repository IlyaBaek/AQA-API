import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UpdateUserDto;
import reseponsesDTO.UsersDto;

import java.util.List;
import java.util.Objects;
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
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(0);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<String> zipCodesAfterCreation = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 200 and user is updated",
                () -> assertEquals(200, updateUserResponse.getResponseCode()),
                () -> assertTrue(usersAfterUpdate.contains(updateUserDto.getUserNewValues()), "Updated user exist"),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserToChange()), "Old user doesn't exist"),
                () -> assertTrue(zipCodesAfterCreation.contains(updateUserDto.getUserToChange().getZipCode()), "User old zip code is added to the list of available zip codes"),
                () -> assertFalse(zipCodesAfterCreation.contains(updateUserDto.getUserNewValues().getZipCode()), "user new zip code is removed from available zip codes list"));
    }

    @Test
    public void updateUserWithUnavailableZipCode() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(3);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<String> zipCodesAfterCreation = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 424 and user is not updated",
                () -> assertEquals(424, updateUserResponse.getResponseCode()),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserNewValues()), "userNewValues doesn't exist in users list"),
                () -> assertTrue(usersAfterUpdate.contains(updateUserDto.getUserToChange()), "userToChange is not updated"),
                () -> assertFalse(zipCodesAfterCreation.contains(updateUserDto.getUserToChange().getZipCode()), "User old zip code is NOT added to the list of available zip codes"),
                () -> assertFalse(zipCodesAfterCreation.contains(updateUserDto.getUserNewValues().getZipCode()), "user new zip code DOESN'T EXIST IN available zip codes list"));
    }

    @Test
    public void updateNewUserValuesWithoutRequiredFields() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(4);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<String> zipCodesAfterCreation = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 409 and user is not updated",
                () -> assertEquals(409, updateUserResponse.getResponseCode()),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserNewValues()), "UserNewValues doesn't exist in users list"),
                () -> assertTrue(usersAfterUpdate.contains(updateUserDto.getUserToChange()), "UserToChange is not updated"),
                () -> assertFalse(zipCodesAfterCreation.contains(updateUserDto.getUserToChange().getZipCode()), "User old zip code is NOT added to the list of available zip codes"),
                () -> assertTrue(zipCodesAfterCreation.contains(updateUserDto.getUserNewValues().getZipCode()), "user new zip code is NOT removed from available zip codes list"));
    }

    @Test
    public void updateUserToChangeWithoutRequiredFields() {
        UpdateUserDto updateUserDto = getUserToUpdateWithDifferentSetOfFields(5);

        ResponseWrapper updateUserResponse = UsersClient.updateUser(updateUserDto);

        List<String> zipCodesAfterCreation = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        List<UsersDto> usersAfterUpdate = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        assertAll("status code is 409 and user is not updated",
                () -> assertEquals(409, updateUserResponse.getResponseCode()),
                () -> assertFalse(usersAfterUpdate.contains(updateUserDto.getUserNewValues()), "UserNewValues doesn't exist in the system"),
                () -> assertFalse(zipCodesAfterCreation.contains(updateUserDto.getUserToChange().getZipCode()), "User old zip code is NOT added to the list of available zip codes"),
                () -> assertTrue(zipCodesAfterCreation.contains(updateUserDto.getUserNewValues().getZipCode()), "user new zip code is NOT removed from available zip codes list"));
    }

    @Step("Different set of users for update")
    public UpdateUserDto getUserToUpdateWithDifferentSetOfFields(int choice) {
        List<UsersDto> currentUsers = (List<UsersDto>) UsersClient.getUsers().getResponseBody();
        UsersDto userToChange = currentUsers.get(random.nextInt(currentUsers.size()));

        String updatedZipCode;
        do {
            ZipCodesClient.createRandomZipCodes(1);
            List<String> availableZipCodes = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();

            updatedZipCode = availableZipCodes.get(random.nextInt(availableZipCodes.size()));
        }
        while (Objects.equals(updatedZipCode, userToChange.getZipCode()));

        UsersDto userNewValues = new UsersDto();
        switch (choice) {
            //All fields are new
            case 0:
                userNewValues.setZipCode(updatedZipCode);
                break;
            //Only required fields are new
            case 1:
                userNewValues.setZipCode(userToChange.getZipCode());
                userNewValues.setAge(userToChange.getAge());
                break;
            //Only non-required fields are new
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
            //empty required fields for userNewValues
            case 4:
                userNewValues.setZipCode(updatedZipCode);
                userNewValues.setName(null);
                break;
            //empty required fields for userToChange
            case 5:
                userNewValues.setZipCode(updatedZipCode);
                userToChange.setName(null);
                userToChange.setZipCode(null);
                break;
        }
        return new UpdateUserDto(userNewValues, userToChange);
    }
}
