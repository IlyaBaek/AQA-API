import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reseponsesDTO.TokenDto;

public class Mapper {
    private TokenDto tokenDto = new TokenDto();
    private ObjectMapper objectMapper = new ObjectMapper();


    public TokenDto tokenStringToObject(String response ) {
        try {

            tokenDto = objectMapper.readValue(response, TokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return tokenDto;

    }
}
