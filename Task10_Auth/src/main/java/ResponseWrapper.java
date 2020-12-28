import reseponsesDTO.UsersDto;

import java.util.ArrayList;

public class ResponseWrapper {
    private int responseCode;
    private ArrayList responseBody;

    public ArrayList getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ArrayList responseBody) {
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
