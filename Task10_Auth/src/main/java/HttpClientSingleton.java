import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Properties;

public class HttpClientSingleton {
    private static HttpClientSingleton object;
    private static CloseableHttpClient httpClient;
    private static CloseableHttpClient httpClientWithProvider;
    private static Properties properties = new LoadProperties().loadProperties();

    private HttpClientSingleton() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(properties.getProperty("username"), properties.getProperty("password"));
        provider.setCredentials(AuthScope.ANY, credentials);

        httpClient = HttpClientBuilder.create().build();

        httpClientWithProvider = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
    }

    public static HttpClientSingleton getInstance() {
        if (object == null) {
            object = new HttpClientSingleton();
        }
        return object;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public CloseableHttpClient getHttpClientWithProvider() {
        return httpClientWithProvider;
    }
}
