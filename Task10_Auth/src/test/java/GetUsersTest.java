import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GetUsersTest {
    @BeforeEach
    public void beforeTest() {
        UsersClient.createUserIfNotExist();
    }

    @Test
    public void getUsers() {
        ResponseWrapper getUsersResponse = UsersClient.getUsers();
        List<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        for (UsersDto usersDto : usersList) {
            System.out.println(usersDto.getAge());
            System.out.println(usersDto.getZipCode());
            System.out.println(usersDto.getName());
            System.out.println(usersDto.getUserSex());
        }
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertNotNull(usersList, "Response has body"));
    }

    @Test
    public void getUsersOlderThan() {
        Integer age = 19;
        ResponseWrapper getUsersResponse = UsersClient.getUsers();
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        List<UsersDto> filteredUserList = usersList.stream().filter(x -> Objects.nonNull(x.getAge())).filter(x -> x.getAge() > age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersOlderThanResponse = UsersClient.getUsers(age, null, null);

        List<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersOlderThanResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersYoungerThan() {
        Integer age = 90;
        ResponseWrapper getUsersResponse = UsersClient.getUsers();
        List<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        List<UsersDto> filteredUserList = usersList.stream().filter(x -> Objects.nonNull(x.getAge())).filter(x -> x.getAge() < age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersYoungerThanResponse = UsersClient.getUsers(null, age, null);

        List<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersYoungerThanResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersBySex() {
        UsersDto.Sex sex = UsersDto.Sex.getRandomSex();
        ResponseWrapper getUsersResponse = UsersClient.getUsers();
        List<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        List<UsersDto> filteredUserList = usersList.stream().filter(x -> x.getUserSex() == sex).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersBySexResponse = UsersClient.getUsers(null, null, sex);

        List<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersBySexResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }
}



