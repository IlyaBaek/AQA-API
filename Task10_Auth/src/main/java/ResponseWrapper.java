import java.util.List;

public class ResponseWrapper {
    private int responseCode;
    private List<String> responseBody;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<String> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(List<String> responseBody) {
        this.responseBody = responseBody;
    }
}
