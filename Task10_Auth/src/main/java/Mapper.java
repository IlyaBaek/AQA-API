import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

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
}
