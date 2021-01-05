import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GetUsersTest {
    @Test
    public void getUsers() {
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        for (UsersDto usersDto : usersList) System.out.println(usersDto.getAge());

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertNotNull(usersList, "Response has body"));
    }

    @Test
    public void getUsersOlderThan() {
        Integer age = 19;
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> Objects.nonNull(x.getAge())).filter(x -> x.getAge() > age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersOlderThanResponse = UsersClient.getUsers(age, null, null);

        ArrayList<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersOlderThanResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersYoungerThan() {
        Integer age = 90;
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> Objects.nonNull(x.getAge())).filter(x -> x.getAge() < age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersYoungerThanResponse = UsersClient.getUsers(null, age, null);

        ArrayList<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersYoungerThanResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersBySex() {
        UsersDto.Sex sex = UsersDto.Sex.getRandomSex();
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = (ArrayList<UsersDto>) getUsersResponse.getResponseBody();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> x.getUserSex() == sex).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersBySexResponse = UsersClient.getUsers(null, null, sex);

        ArrayList<UsersDto> resultUserList = (ArrayList<UsersDto>) getUsersBySexResponse.getResponseBody();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }
}



