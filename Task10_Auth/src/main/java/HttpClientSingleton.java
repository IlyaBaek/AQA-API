import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpClientSingleton {
    private static HttpClientSingleton object;
    private CloseableHttpClient httpClient;
    private CloseableHttpClient httpClientWithProvider;

    private HttpClientSingleton() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(PropertiesReader.get("username"), PropertiesReader.get("password"));
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
