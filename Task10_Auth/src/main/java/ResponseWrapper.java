import reseponsesDTO.UsersDto;

import java.util.ArrayList;
import java.util.List;

public class ResponseWrapper {
    private int responseCode;
    private List<String> responseBodyZipCodes;
    private ArrayList<UsersDto> responseBodyUsers;

    public ArrayList<UsersDto> getResponseBodyUsers() {
        return responseBodyUsers;
    }

    public void setResponseBodyUsers(ArrayList<UsersDto> responseBodyUsers) {
        this.responseBodyUsers = responseBodyUsers;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<String> getResponseBodyZipCodes() {
        return responseBodyZipCodes;
    }

    public void setResponseBodyZipCodes(List<String> responseBody) {
        this.responseBodyZipCodes = responseBody;
    }
}
