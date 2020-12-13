import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import reseponsesDTO.TokenDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AuthSingleton {
    private static AuthSingleton object;
    private static TokenDto tokenDto;
    private TokenDto writeToken;
    private TokenDto readToken;

    private AuthSingleton() {
        readToken = getToken(PropertiesReader.get("readAccess"));
        writeToken = getToken(PropertiesReader.get("writeAccess"));
    }

    public static AuthSingleton getInstance() {
        if (object == null) {
            object = new AuthSingleton();
        }
        return object;
    }

    public String getReadToken() {
        return "Bearer " + readToken.getAccessToken();
    }


    public String getWriteToken() {
        return "Bearer " + writeToken.getAccessToken();
    }

    private static TokenDto getToken(String accessType) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("tokenURI"));
        ArrayList<BasicNameValuePair> tokenConfig = new ArrayList<>();
        tokenConfig.add(new BasicNameValuePair("grant_type", "client_credentials"));
        tokenConfig.add(new BasicNameValuePair("scope", accessType));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(tokenConfig));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClientWithProvider().execute(httpPost);
            tokenDto = Mapper.entityToObj(response.getEntity(), TokenDto.class);
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
        return tokenDto;
    }
}

