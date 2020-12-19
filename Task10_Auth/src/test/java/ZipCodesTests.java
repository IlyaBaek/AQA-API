import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodesTests {
    @Test
    public void getZipCodesTest() {
        ZipCodesClient zipCodesClient = new ZipCodesClient();
        ResponseWrapper responseWrapper = zipCodesClient.getZipCodes();
        List<String> getZipCodesResponseBody = new ArrayList<>(responseWrapper.getResponseBody());

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, responseWrapper.getResponseCode(),
                        "Bug: GET request should return 200"),
                () -> assertNotNull(getZipCodesResponseBody, "Response has body"));
    }

    @Test
    public void addZipCodesTest() {
        ZipCodesClient zipCodesClient = new ZipCodesClient();
        ResponseWrapper responseGet = zipCodesClient.getZipCodes();
        List<String> availableZipCodes = new ArrayList<>(responseGet.getResponseBody());

        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(RandomStringUtils.random(5, false, true));
        newZipCodes.add(RandomStringUtils.random(5, false, true));

        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = zipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = responsePost.getResponseBody();

        assertAll("Status code is 201 and Zip codes from request body are added to available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }

    @Test
    public void addDuplicatesZipCodesAndCheckThereAreNoDuplicationsTest() {
        ZipCodesClient zipCodesClient = new ZipCodesClient();
        ResponseWrapper responseGet = zipCodesClient.getZipCodes();
        List<String> availableZipCodes = responseGet.getResponseBody();

        Random random = new Random();
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));

        List<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        ResponseWrapper responsePost = zipCodesClient.postZipCodes(newZipCodes);
        List<String> resultZipCodes = responsePost.getResponseBody();
        List<String> resultZipCodesWithoutDuplications = new ArrayList<>(new HashSet<>(resultZipCodes));

        assertAll("Status code is 201 // Zip codes from request body are added to available zip codes //  There are no duplications in available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(resultZipCodesWithoutDuplications, resultZipCodes),
                () -> assertEquals(201, responsePost.getResponseCode()));
    }
}
