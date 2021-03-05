import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import reseponsesDTO.UpdateUserDto;
import reseponsesDTO.UsersDto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsersClient {
    @Step("POST upload users")
    public static ResponseWrapper uploadUsers(File usersFile) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("appURI") + PropertiesReader.get("uploadUsersURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", usersFile, ContentType.MULTIPART_FORM_DATA, usersFile.getName())
                    .build();
            httpPost.setEntity(entity);

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            //String uploadUsersResponseBody = Mapper.entityToObj(response.getEntity(), String.class);
            String responseString = new BasicResponseHandler().handleResponse(response);
            responseWrapper.setResponseBody(responseString);
            Allure.addAttachment("Number of uploaded users", responseString);
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

    @Step("POST delete user")
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
            Allure.addAttachment("User to remove", Mapper.UserDtoToString(usersDto));
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

    @Step("PUT update user")
    public static ResponseWrapper updateUser(UpdateUserDto updatedUserInfo) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(updatedUserInfo);
            StringEntity entity = new StringEntity(json);
            if (UpdateUserDto.allFieldsAreNew(updatedUserInfo)) {
                HttpPatch httpPatch = new HttpPatch(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
                httpPatch.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
                httpPatch.setEntity(entity);
                httpPatch.setHeader("Accept", "*/*");
                httpPatch.setHeader("Content-type", "application/json");

                response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPatch);
            } else {
                HttpPut httpPut = new HttpPut(PropertiesReader.get("appURI") + PropertiesReader.get("usersURI"));
                httpPut.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
                httpPut.setEntity(entity);
                httpPut.setHeader("Accept", "*/*");
                httpPut.setHeader("Content-type", "application/json");

                response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPut);
            }
            assert response != null;
            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            Allure.addAttachment("User to update", Mapper.UserDtoToString(updatedUserInfo.getUserToChange()));
            Allure.addAttachment("User new values", Mapper.UserDtoToString(updatedUserInfo.getUserNewValues()));
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

    @Step("POST create user")
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
            Allure.addAttachment("Created user", Mapper.UserDtoToString(usersDto));
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

    @Step("GET users with parameters")
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
            Allure.addAttachment("Users", Mapper.UserDtoToString(usersResponseBody));
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
        ResponseWrapper responseWrapper = getUsers();
        List<UsersDto> usersDtoList = (List<UsersDto>) responseWrapper.getResponseBody();
        if (usersDtoList.isEmpty()) {
            UsersDto usersDto = new UsersDto();
            List<String> availableZipCodes = (List<String>) ZipCodesClient.getZipCodes().getResponseBody();
            usersDto.setZipCode(availableZipCodes.get(random.nextInt(availableZipCodes.size())));
            createUser(usersDto);
        }
    }
}
