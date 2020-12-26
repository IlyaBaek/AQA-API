import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static reseponsesDTO.UsersDto.randomSex;

public class CreateUsersTests {
    private ZipCodesClient zipCodesClient = new ZipCodesClient();
    private UsersClient usersClient = new UsersClient();
    private static final Random random = new Random();

    @Test
    public void createUserTest() {
        ResponseWrapper getZipCodesResponseBeforeCreation = zipCodesClient.getZipCodes();
        int age = RandomUtils.nextInt(1, 99);
        String name = RandomStringUtils.random(5, true, false);
        UsersDto.sex userSex = randomSex();
        int zipCodesSize = getZipCodesResponseBeforeCreation.getResponseBodyZipCodes().size();
        String exactZipCode = getZipCodesResponseBeforeCreation.getResponseBodyZipCodes().get(random.nextInt(zipCodesSize));
        int exactZipCodesCountBeforeCreation = Collections.frequency(getZipCodesResponseBeforeCreation.getResponseBodyZipCodes(), exactZipCode);

        UsersDto userToAdd = new UsersDto();
        userToAdd = userToAdd.createUserDtoObject(age, name, userSex, exactZipCode);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);


        ResponseWrapper getZipCodesResponseAfterCreation = zipCodesClient.getZipCodes();
        int exactZipCodesCountAfterCreation = Collections.frequency(getZipCodesResponseAfterCreation.getResponseBodyZipCodes(), exactZipCode);
        UsersDto addedUser = new UsersDto();
        ResponseWrapper getUsersResponse = usersClient.getUsers();
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        if (usersList.contains(userToAdd)) {
            addedUser = usersList.get(usersList.indexOf(userToAdd));
        }
        UsersDto finalUserToAdd = userToAdd;
        UsersDto finalAddedUser = addedUser;
        assertAll("Status code is 201 // user is added, zip code is removed",
                () -> assertEquals(exactZipCodesCountBeforeCreation - 1, exactZipCodesCountAfterCreation),
                //() -> assertTrue(usersList.contains(finalUserToAdd)),
                () -> assertEquals(finalUserToAdd, finalAddedUser),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithRequiredFieldsTest() {
        String name = RandomStringUtils.random(5, true, false);
        UsersDto.sex userSex = randomSex();
        UsersDto userToAdd = new UsersDto();
        userToAdd = userToAdd.createUserDtoObject(name, userSex);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = usersClient.getUsers();
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        UsersDto addedUser = new UsersDto();
        if (usersList.contains(userToAdd)) {
            addedUser = usersList.get(usersList.indexOf(userToAdd));
        }
        UsersDto finalAddedUser = addedUser;
        UsersDto finalUserToAdd = userToAdd;
        assertAll("Status code is 201 // user is added",
                () -> assertEquals(finalUserToAdd, finalAddedUser),
                () -> assertEquals(201, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithIncorrectZipCodeTest() {
        ResponseWrapper getZipCodesResponse = zipCodesClient.getZipCodes();
        int age = RandomUtils.nextInt(1, 99);
        String name = RandomStringUtils.random(5, true, false);
        UsersDto.sex userSex = randomSex();
        String zipCode;
        do {
            zipCode = RandomStringUtils.random(5, false, true);
        } while (getZipCodesResponse.getResponseBodyZipCodes().contains(zipCode));
        UsersDto userToAdd = new UsersDto();
        userToAdd = userToAdd.createUserDtoObject(age, name, userSex, zipCode);

        ResponseWrapper createUserResponse = usersClient.createUser(userToAdd);

        ResponseWrapper getUsersResponse = usersClient.getUsers();
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        UsersDto finalUserToAdd = userToAdd;
        assertAll("Status code is 424 // user is NOT added",
                () -> assertFalse(usersList.contains(finalUserToAdd)),
                () -> assertEquals(424, createUserResponse.getResponseCode()));
    }

    @Test
    public void createUserWithDuplicateNameAndSexTest() {
        ResponseWrapper getUsersBeforeAddingDuplicateResponse = usersClient.getUsers();
        ArrayList<UsersDto> usersListBeforeAddingDuplicate = getUsersBeforeAddingDuplicateResponse.getResponseBodyUsers();
        UsersDto userWithDuplicateNameAndSex = usersListBeforeAddingDuplicate.get(random.nextInt(usersListBeforeAddingDuplicate.size()));

        ResponseWrapper createUserResponse = usersClient.createUser(userWithDuplicateNameAndSex);

        ResponseWrapper getUsersResponseAfterAddingDuplicate = usersClient.getUsers();
        ArrayList<UsersDto> usersListAfterAddingDuplicate = getUsersResponseAfterAddingDuplicate.getResponseBodyUsers();
        assertAll("Status code is 400 // user is NOT added",
                () -> assertEquals(usersListBeforeAddingDuplicate, usersListAfterAddingDuplicate),
                () -> assertEquals(400, createUserResponse.getResponseCode()));
    }
}
