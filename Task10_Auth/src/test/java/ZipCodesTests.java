import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodesTests {
    private ZipCodesClient zipCodesClient = new ZipCodesClient();

    @Test
    public void getZipCodesTest() {
        ResponseWrapper responseWrapper = zipCodesClient.getZipCodes();
        List<String> getZipCodesResponseBody = new ArrayList<>(responseWrapper.getResponseBody());

        System.out.println(getZipCodesResponseBody);

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, responseWrapper.getResponseCode(),
                        "Bug: GET request should return 200"),
                () -> assertNotNull(getZipCodesResponseBody, "Response has body"));
    }

    @Test
    public void addZipCodesTest() {
        ResponseWrapper responseGet = zipCodesClient.getZipCodes();
        List<String> availableZipCodes = new ArrayList<>(responseGet.getResponseBody());

        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(RandomStringUtils.random(5, false, true));
        newZipCodes.add(RandomStringUtils.random(5, false, true));

        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = zipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = new ArrayList<>(responsePost.getResponseBody());

        assertAll("Status code is 201 and Zip codes from request body are added to available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }

    @Test
    public void addDuplicatesZipCodesAndCheckThereAreNoDuplicationsTest() {
        ZipCodesClient.createZipCodeIfNotExist(zipCodesClient);
        ResponseWrapper responseGet = zipCodesClient.getZipCodes();
        List<String> availableZipCodes = new ArrayList<>(responseGet.getResponseBody());

        Random random = new Random();
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));

        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = zipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = new ArrayList<>(responsePost.getResponseBody());
        List<String> resultZipCodesWithoutDuplications = new ArrayList<>(new HashSet<>(resultZipCodes));

        assertAll("Status code is 201 // Zip codes from request body are added to available zip codes //  There are no duplications in available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(resultZipCodesWithoutDuplications, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }

    @Test
    public void addAlreadyUsedZipCodeAndCheckThereIsNoDuplicationInUsedAndNotUsedZipCodes() {
        UsersClient usersClient = new UsersClient();
        UsersClient.createUserIfNotExist(usersClient);
        ResponseWrapper responseGet = zipCodesClient.getZipCodes();
        List<String> availableZipCodes = new ArrayList<>(responseGet.getResponseBody());
        ResponseWrapper getUsersResponse = usersClient.getUsers();
        List<UsersDto> usersList = new ArrayList<>(getUsersResponse.getResponseBody());
        Random random = new Random();
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(usersList.get(random.nextInt(usersList.size())).getZipCode());
        newZipCodes.add(usersList.get(random.nextInt(usersList.size())).getZipCode());
        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = zipCodesClient.postZipCodes(newZipCodes);

        List<String> zipCodesAfterAdding = new ArrayList<>(responsePost.getResponseBody());
        List<String> allZipCodes = usersList
                .stream()
                .map(UsersDto::getZipCode)
                .collect(Collectors.toList());
        allZipCodes.addAll(zipCodesAfterAdding);
        List<String> allZipCodesWithoutDuplications = new ArrayList<>(new HashSet<>(allZipCodes));
        assertAll("Status 201 // Zip codes added // There is no duplication between used and not used zipCodes",
                () -> assertEquals(expectedZipCodesAfterPost, zipCodesAfterAdding),
                () -> assertEquals(allZipCodesWithoutDuplications, allZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }
}
