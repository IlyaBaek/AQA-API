import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import reseponsesDTO.UsersDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsersClient {
    public ResponseWrapper createUser(UsersDto usersDto) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(usersDto);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            //responseWrapper.setResponseBodyUsers(Mapper.entityToObj(response.getEntity(),UsersDto.class););
            //NOT APPLICABLE BECAUSE OF THE BUG(POST users doesn't return body of created user)
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

    public ResponseWrapper getUsers() {
        HttpGet httpGet = new HttpGet(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
        httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            responseWrapper.setResponseBody(Stream.of(Mapper.entityToObj(response.getEntity(), UsersDto[].class)).collect(Collectors.toCollection(ArrayList::new)));
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

    public static void createUserIfNotExist(UsersClient usersClient) {
        ZipCodesClient zipCodesClient = new ZipCodesClient();
        ZipCodesClient.createZipCodeIfNotExist(zipCodesClient);
        ResponseWrapper responseWrapper = usersClient.getUsers();
        List<UsersDto> usersDtoList = new ArrayList<>(responseWrapper.getResponseBody());
        if (usersDtoList.isEmpty()) {
            UsersDto usersDto = new UsersDto();
            usersClient.createUser(usersDto);
        }
    }
}