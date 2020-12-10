import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AuthSingleton {
    private static AuthSingleton object;
    private static String write_token;
    private static String read_token;
    private static final String TOKEN_URI = "http://localhost:5353/oauth/token";
    private static final String PASSWORD = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String USERNAME = "0oa157tvtugfFXEhU4x7";

    private AuthSingleton() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
        provider.setCredentials(AuthScope.ANY, credentials);
        HttpPost httpPost = new HttpPost(TOKEN_URI);
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
        params.add(new BasicNameValuePair("scope", "read"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException var20) {
            var20.printStackTrace();
        }

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = null;

        try {
            response = client.execute(httpPost);
        } catch (IOException var19) {
            var19.printStackTrace();
        }

        String tokenInfo = null;

        try {
            assert response != null;

            tokenInfo = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException var18) {
            var18.printStackTrace();
        }

        JsonObject jsonObject = (new JsonParser()).parse(tokenInfo).getAsJsonObject();
        read_token = jsonObject.get("access_token").getAsString();
        HttpPost httpPost2 = new HttpPost(TOKEN_URI);
        List<NameValuePair> params2 = new ArrayList();
        params2.add(new BasicNameValuePair("grant_type", "client_credentials"));
        params2.add(new BasicNameValuePair("scope", "write"));

        try {
            httpPost2.setEntity(new UrlEncodedFormEntity(params2));
        } catch (UnsupportedEncodingException var17) {
            var17.printStackTrace();
        }

        HttpClient client2 = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response2 = null;

        try {
            response2 = client2.execute(httpPost2);
        } catch (IOException var16) {
            var16.printStackTrace();
        }

        String tokenInfo2 = null;

        try {
            assert response2 != null;

            tokenInfo2 = EntityUtils.toString(response2.getEntity(), "UTF-8");
        } catch (IOException var15) {
            var15.printStackTrace();
        }

        JsonObject jsonObject2 = (new JsonParser()).parse(tokenInfo2).getAsJsonObject();
        write_token = jsonObject2.get("access_token").getAsString();
    }

    public static AuthSingleton getInstance() {
        if (object == null) {
            object = new AuthSingleton();
        }

        return object;
    }

    public String getReadToken() {
        return "Bearer" + read_token;
    }

    public String getWriteToken() {
        return "Bearer" + write_token;
    }
}
