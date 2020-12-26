import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GetUsersTests {
    UsersClient usersClient = new UsersClient();

    @Test
    public void getUsers() {
        ResponseWrapper getUsersResponse = usersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertNotNull(usersList, "Response has body"));
    }

    @Test
    public void getUsersOlderThan() {
        int age = 19;
        ResponseWrapper getUsersResponse = usersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> x.getAge() > age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersOlderThanResponse = usersClient.getUsers(age, null, null);

        ArrayList<UsersDto> resultUserList = getUsersOlderThanResponse.getResponseBodyUsers();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersYoungerThan() {
        int age = 19;
        ResponseWrapper getUsersResponse = usersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> x.getAge() < age).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersYoungerThanResponse = usersClient.getUsers(null, age, null);

        ArrayList<UsersDto> resultUserList = getUsersYoungerThanResponse.getResponseBodyUsers();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }

    @Test
    public void getUsersBySex() {
        UsersDto.sex sex = UsersDto.randomSex();
        ResponseWrapper getUsersResponse = usersClient.getUsers(null, null, null);
        ArrayList<UsersDto> usersList = getUsersResponse.getResponseBodyUsers();
        ArrayList<UsersDto> filteredUserList = usersList.stream().filter(x -> x.getUserSex() == sex).collect(Collectors.toCollection(ArrayList::new));

        ResponseWrapper getUsersBySexResponse = usersClient.getUsers(null, null, sex);

        ArrayList<UsersDto> resultUserList = getUsersBySexResponse.getResponseBodyUsers();
        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, getUsersResponse.getResponseCode()),
                () -> assertEquals(filteredUserList, resultUserList));
    }
}
