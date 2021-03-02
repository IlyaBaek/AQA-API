import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import reseponsesDTO.UsersDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Mapper {
    public static <T> T entityToObj(HttpEntity entity, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        String body;
        try {
            body = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Can't transform http entity to string");
        }
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't map to the object of " + clazz.getName());
        }
    }

    public static String UserDtoToString(List<UsersDto> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        String usersString = null;
        try {
            usersString = objectMapper.writeValueAsString(users);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return usersString;
    }

    public static String UserDtoToString(UsersDto users) {
        ObjectMapper objectMapper = new ObjectMapper();
        String usersString = null;
        try {
            usersString = objectMapper.writeValueAsString(users);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return usersString;
    }

}
