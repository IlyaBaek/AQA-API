import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZipCodesClient {
    public ResponseWrapper getZipCodes() {
        HttpGet httpGet = new HttpGet(PropertiesReader.get("appURI") + PropertiesReader.get("zipCodesURI"));
        httpGet.addHeader("Authorization", AuthSingleton.getInstance().getReadToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpGet);

            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            responseWrapper.setResponseBodyZipCodes(Stream.of(Mapper.entityToObj(response.getEntity(), String[].class)).collect(Collectors.toCollection(ArrayList::new)));
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
        return responseWrapper;
    }

    public ResponseWrapper postZipCodes(List<String> body) {
        HttpPost httpPost = new HttpPost(PropertiesReader.get("appURI") + PropertiesReader.get("zipCodesExpandURI"));
        httpPost.addHeader("Authorization", AuthSingleton.getInstance().getWriteToken());
        ResponseWrapper responseWrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(body.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");

            response = HttpClientSingleton.getInstance().getHttpClient().execute(httpPost);
            responseWrapper.setResponseCode(response.getStatusLine().getStatusCode());
            responseWrapper.setResponseBodyZipCodes(Stream.of(Mapper.entityToObj(response.getEntity(), String[].class)).collect(Collectors.toCollection(ArrayList::new)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response != null;
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseWrapper;
    }
}
