import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class Auth_Premetive_Basic {
    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://localhost:5353/oauth/token";
    private static final String DEFAULT_PASS = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String DEFAULT_USER = "0oa157tvtugfFXEhU4x7";

    public Auth_Premetive_Basic() {
    }

    public static void main(String[] args) throws IOException {
        /*HttpHost targetHost = new HttpHost("localhost", 5353, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("0oa157tvtugfFXEhU4x7", "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq"));
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet("http://localhost:5353/oauth/token"), context);
        int statusCode = response.getStatusLine().getStatusCode();*/


        HttpHost targetHost = new HttpHost("localhost", 8082, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(
                new HttpPost(URL_SECURED_BY_BASIC_AUTHENTICATION), context);

        int statusCode = response.getStatusLine().getStatusCode();
        //assertThat(statusCode, equalTo(HttpStatus.SC_OK));

        System.out.println(200);
        System.out.println(statusCode);
    }
}
