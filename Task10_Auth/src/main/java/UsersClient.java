import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import reseponsesDTO.UsersDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsersClient {
    public static ResponseWrapper deleteUser(UsersDto usersDto) {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
        httpDelete.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(usersDto);
            StringEntity entity = new StringEntity(json);
            httpDelete.setEntity(entity);
            httpDelete.setHeader("Accept", "*/*");
            httpDelete.setHeader("Content-type", "application/json");

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpDelete);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
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

    public static ResponseWrapper createUser(UsersDto usersDto) {
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

    public static ResponseWrapper getUsers() {
        return getUsers(null, null, null);
    }

    public static ResponseWrapper getUsers(Integer olderThan, Integer youngerThan, UsersDto.Sex sex) {
        URIBuilder builder;
        HttpGet httpGet = null;
        try {
            builder = new URIBuilder(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
            if (olderThan != null) {
                builder.setParameter("olderThan", String.valueOf(olderThan));
            }
            if (youngerThan != null) {
                builder.setParameter("youngerThan", String.valueOf(youngerThan));
            }
            if (sex != null) {
                builder.setParameter("sex", String.valueOf(sex));
            }
            httpGet = new HttpGet(builder.build());
            httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            List<UsersDto> usersResponseBody = Stream.of(Mapper.entityToObj(response.getEntity(), UsersDto[].class)).collect(Collectors.toCollection(ArrayList::new));
            responseWrapper.setResponseBody(usersResponseBody);
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

    public static void createUserIfNotExist() {
        Random random = new Random();
        ZipCodesClient.createZipCodeIfNotExist();
        List<String> availableZipCodes = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
        ResponseWrapper responseWrapper = getUsers();
        List<UsersDto> usersDtoList = (List<UsersDto>) responseWrapper.getResponseBody();
        if (usersDtoList.isEmpty()) {
            UsersDto usersDto = new UsersDto();
            usersDto.setZipCode(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
            createUser(usersDto);
        }
    }
}
