import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodesTests {

    @Test
    @Issue("Bug: GET request should return 200 instead of 201")
    public void getZipCodesTest() {
        ResponseWrapper responseWrapper = ZipCodesClient.getZipCodes();
        List<String> getZipCodesResponseBody = (List<String>) responseWrapper.getResponseBody();

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, responseWrapper.getResponseCode(),
                        "Bug: GET request should return 200"),
                () -> assertNotNull(getZipCodesResponseBody, "Response has body"));
    }

    @Test
    public void addZipCodesTest() {
        List<String> availableZipCodes = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();

        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(RandomStringUtils.random(5, false, true));
        newZipCodes.add(RandomStringUtils.random(5, false, true));

        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = ZipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = (List<String>) responsePost.getResponseBody();
        assertAll("Status code is 201 and Zip codes from request body are added to available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }

    @Test
    public void addDuplicatesZipCodesAndCheckThereAreNoDuplicationsTest() {
        ZipCodesClient.createZipCodeIfNotExist();
        ResponseWrapper responseGet = ZipCodesClient.getZipCodes();
        List<String> availableZipCodes = (List<String>) responseGet.getResponseBody();

        Random random = new Random();
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));

        ResponseWrapper responsePost = ZipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = (List<String>) responsePost.getResponseBody();
        List<String> resultZipCodesWithoutDuplications = new ArrayList<>(new HashSet<>(resultZipCodes));
        Collections.sort(resultZipCodesWithoutDuplications);
        Collections.sort(resultZipCodes);
        assertAll("Status code is 201 // Zip codes from request body are added to available zip codes //  There are no duplications in available zip codes",
                () -> assertTrue(resultZipCodes.containsAll(newZipCodes), "there is no new zip codes in the result zip codes list"),
                () -> assertEquals(resultZipCodesWithoutDuplications, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }

    @Test
    public void addAlreadyUsedZipCodeAndCheckThereIsNoDuplicationInUsedAndNotUsedZipCodes() {
        UsersClient.createUserIfNotExist();
        ZipCodesClient.createZipCodeIfNotExist();
        ResponseWrapper getUsersResponse = UsersClient.getUsers(null, null, null);
        List<UsersDto> usersList = (List<UsersDto>) getUsersResponse.getResponseBody();
        Random random = new Random();
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(usersList.get(random.nextInt(usersList.size())).getZipCode());

        ResponseWrapper responsePost = ZipCodesClient.postZipCodes(newZipCodes);

        List<String> zipCodesAfterAdding = (List<String>) responsePost.getResponseBody();
        List<String> allZipCodes = usersList
                .stream()
                .map(UsersDto::getZipCode)
                .collect(Collectors.toList());
        allZipCodes.addAll(zipCodesAfterAdding);
        List<String> allZipCodesWithoutDuplications = new ArrayList<>(new HashSet<>(allZipCodes));
        Collections.sort(allZipCodesWithoutDuplications);
        Collections.sort(allZipCodes);
        assertAll("Status 201 // Zip codes added // There is no duplication between used and not used zipCodes",
                () -> assertFalse(zipCodesAfterAdding.containsAll(newZipCodes), "Duplicate already used zip code is added to list of available zip codes"),
                () -> assertEquals(allZipCodesWithoutDuplications, allZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }
}
