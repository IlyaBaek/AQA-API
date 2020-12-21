package reseponsesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersDto {
    private int age;
    private String name;
    private String zipCode;
    @JsonProperty("sex")
    public sex userSex;

    public enum sex {FEMALE, MALE}

    public void setUserSex(sex input) {
        userSex = input;
    }

    public sex getUserSex() {
        return userSex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
