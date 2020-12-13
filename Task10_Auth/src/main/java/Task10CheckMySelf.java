import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;
import java.io.PrintStream;

public class Task10CheckMySelf {
    public Task10CheckMySelf() {
    }

    public static void main(String[] args) throws IOException {
        String readToken = AuthSingleton.getInstance().getReadToken();
        String writeToken = AuthSingleton.getInstance().getWriteToken();
        System.out.println(readToken);
        System.out.println(writeToken);

        getUsers();
        //postUsers();
    }

    public static void getUsers() throws IOException {
        String URL_GET = "http://localhost:5353/users";
        HttpGet httpGet = new HttpGet(URL_GET);
        httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        CloseableHttpResponse response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);
        System.out.println(response.getStatusLine().getStatusCode());
        Header[] headers2 = response.getAllHeaders();

        for (Header header : headers2) {
            PrintStream var10000 = System.out;
            String var10001 = header.getName();
            var10000.println("Key [ " + var10001 + "], Value[ " + header.getValue() + " ]");
        }

        System.out.println((new BasicResponseHandler()).handleResponse(response));
        response.close();
    }

    public static void postUsers() throws IOException {
        String URL_POST = "http://localhost:5353/users";
        HttpPost httpPost = new HttpPost(URL_POST);
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        CloseableHttpResponse response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        Header[] headers2 = response.getAllHeaders();

        for (Header header : headers2) {
            PrintStream var10000 = System.out;
            String var10001 = header.getName();
            var10000.println("Key [ " + var10001 + "], Value[ " + header.getValue() + " ]");
        }
        System.out.println((new BasicResponseHandler()).handleResponse(response));
    }
}