import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZipCodesClient {
    public static ResponseWrapper getZipCodes() {
        HttpGet httpGet = new HttpGet(PropertiesReader.get("appURI") + PropertiesReader.get("zipCodesURI"));
        httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            List<String> zipCodesResponseBody = Stream.of(Mapper.entityToObj(response.getEntity(), String[].class)).collect(Collectors.toCollection(ArrayList::new));
            responseWrapper.setResponseBody(zipCodesResponseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseWrapper;
    }

    public static ResponseWrapper postZipCodes(List<String> body) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("appURI") + PropertiesReader.get("zipCodesExpandURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);
            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            List<String> zipCodesResponseBody = Stream.of(Mapper.entityToObj(response.getEntity(), String[].class)).collect(Collectors.toCollection(ArrayList::new));
            responseWrapper.setResponseBody(zipCodesResponseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response != null;
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseWrapper;
    }

    public static void createZipCodeIfNotExist() {
        List<String> zipCodesList = (List<String>) getZipCodes().getResponseBody();
        if (zipCodesList.isEmpty()) {
            List<String> newZipCodes = new ArrayList<>();
            newZipCodes.add(RandomStringUtils.random(5, false, true));
            newZipCodes.add(RandomStringUtils.random(5, false, true));
            postZipCodes(newZipCodes);
        }
    }

    public static void createRandomZipCodes(){
        List<String> newZipCodes = new ArrayList<>();
        newZipCodes.add(RandomStringUtils.random(5, false, true));
        postZipCodes(newZipCodes);
    }

    public static String getRandomNotExistingZipCode() {
        ResponseWrapper getZipCodesResponse = ZipCodesClient.getZipCodes();
        String zipCode;
        List<String> zipCodesList = (List<String>) getZipCodesResponse.getResponseBody();
        do {
            zipCode = RandomStringUtils.random(5, false, true);
        } while (zipCodesList.contains(zipCode));
        return zipCode;
    }
}
