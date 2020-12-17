import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodesTests {
    @Test
    public void getZipCodesTest() {
        CloseableHttpResponse response = getZipCodes();
        ArrayList<String> getZipCodesResponseBody = responseStringToArrayList(response);

        assertAll("Response has body and status code is 200",
                () -> assertEquals(200, response.getStatusLine().getStatusCode(),
                        "Bug: GET request should return 200"),
                () -> assertNotNull(getZipCodesResponseBody, "Response has body"));

    }

    @Test
    public void scenario2Test() {
        CloseableHttpResponse getResponse = getZipCodes();
        ArrayList<String> availableZipCodes = responseStringToArrayList(getResponse);

        ArrayList<String> newZipCodes = new ArrayList<>();
        newZipCodes.add("99999");
        newZipCodes.add("88888");

        ArrayList<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        CloseableHttpResponse postResponse = postZipCodes(newZipCodes);
        ArrayList<String> resultZipCodes = responseStringToArrayList(postResponse);
        assertAll("Status code is 201 and Zip codes from request body are added to available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(201, postResponse.getStatusLine().getStatusCode()));
    }

    @Test
    public void scenario3Test() {
        CloseableHttpResponse getResponse = getZipCodes();
        ArrayList<String> availableZipCodes = responseStringToArrayList(getResponse);

        Random random = new Random();
        ArrayList<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
        newZipCodes.add(availableZipCodes.get(random.nextInt(availableZipCodes.size())));

        ArrayList<String> expectedZipCodesAfterPost = new ArrayList<>();
        expectedZipCodesAfterPost.addAll(availableZipCodes);
        expectedZipCodesAfterPost.addAll(newZipCodes);

        CloseableHttpResponse postResponse = postZipCodes(newZipCodes);
        ArrayList<String> resultZipCodes = responseStringToArrayList(postResponse);

        HashSet<String> resultZipCodesWithoutDuplications = new HashSet<>(resultZipCodes);

        assertAll("Status code is 201 // Zip codes from request body are added to available zip codes //  There are no duplications in available zip codes",
                () -> assertEquals(expectedZipCodesAfterPost, resultZipCodes),
                () -> assertEquals(resultZipCodesWithoutDuplications, resultZipCodes),
                () -> assertEquals(201, postResponse.getStatusLine().getStatusCode()));
    }

    private CloseableHttpResponse getZipCodes() {
        HttpGet httpGet = new HttpGet(PropertiesReader.get("zipCodesURI"));
        httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);
            //response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private CloseableHttpResponse postZipCodes(ArrayList<String> body) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("zipCodesExpandURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);
            //response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private ArrayList<String> responseStringToArrayList(CloseableHttpResponse response) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            arrayList = new ArrayList<>(Arrays.asList(new BasicResponseHandler().handleResponse(response).replaceAll("[\\[\\]\"]", "").split(",")));
            response.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
