import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.junit.jupiter.api.Test;
import reseponsesDTO.UsersDto;

import java.io.IOException;

public class UsersTests {

    @Test
    public void createUserTest() throws IOException {
        CloseableHttpResponse response = createUser();

        System.out.println(new BasicResponseHandler().handleResponse(response));
    }

    public CloseableHttpResponse createUser() {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("appURI")+PropertiesReader.get("usersURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        CloseableHttpResponse response = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(createUserDtoObject(19, "Ilia", UsersDto.sex.FEMALE, "99999"));
            StringEntity entity = new StringEntity(json);
            System.out.println(json);
            System.out.println(entity);
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

    public UsersDto createUserDtoObject(int age, String name, UsersDto.sex sex, String zipCode) {
        UsersDto usersDto = new UsersDto();
        usersDto.setAge(age);
        usersDto.setName(name);
        usersDto.setUserSex(sex);
        usersDto.setZipCode(zipCode);
        return usersDto;
    }
}
