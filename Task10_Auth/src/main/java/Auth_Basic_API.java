//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Auth_Basic_API {
    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://localhost:5353/oauth/token";
    private static final String DEFAULT_PASS = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String DEFAULT_USER = "0oa157tvtugfFXEhU4x7";

    public Auth_Basic_API() {
    }

    public static void main(String[] args) throws IOException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS);
        provider.setCredentials(AuthScope.ANY, credentials);
        HttpPost httpPost = new HttpPost(URL_SECURED_BY_BASIC_AUTHENTICATION);
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
        params.add(new BasicNameValuePair("scope", "read"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
    }
}
