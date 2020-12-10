import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Auth_Raw_HTTP_Headers {
    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://localhost:5353/oauth/token";
    private static final String DEFAULT_PASS = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String DEFAULT_USER = "0oa157tvtugfFXEhU4x7";

    public static void main(String[] args) throws IOException {
        HttpPost request = new HttpPost(URL_SECURED_BY_BASIC_AUTHENTICATION);
        String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        //request.setHeader("grant_type", "client_credentials");
        //request.setHeader("scope", "read");

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        //assertThat(statusCode, equalTo(HttpStatus.SC_OK));
        System.out.println(HttpStatus.SC_OK);
        System.out.println(statusCode);
    }
}
