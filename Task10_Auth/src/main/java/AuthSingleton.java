import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import reseponsesDTO.TokenDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

public class AuthSingleton {
    private static AuthSingleton object;
    private static String writeToken;
    private static String readToken;
    private Properties properties = new LoadProperties().loadProperties();

    private AuthSingleton() {
        readToken = getToken(properties.getProperty("readAccess"));
        writeToken = getToken(properties.getProperty("writeAccess"));
    }

    public static AuthSingleton getInstance() {
        if (object == null) {
            object = new AuthSingleton();
        }

        return object;
    }

    public String getReadToken() {
        return "Bearer " + readToken;
    }


    public String getWriteToken() {
        return "Bearer " + writeToken;
    }

    private String getToken(String accessType) {
        HttpPost httpPost = new HttpPost(properties.getProperty("tokenURI"));
        ArrayList<BasicNameValuePair> tokenConfig = new ArrayList<>();
        tokenConfig.add(new BasicNameValuePair("grant_type", "client_credentials"));
        tokenConfig.add(new BasicNameValuePair("scope", accessType));

        String token = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(tokenConfig));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClientWithProvider().execute(httpPost);
            String tokenResposeString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Mapper mapper = new Mapper();
            TokenDto tokenDto = mapper.tokenStringToObject(tokenResposeString);
            token = tokenDto.getAccess_token();
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
        return token;
    }
}

