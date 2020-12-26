package reseponsesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UsersDto {
    private int age;
    private String name;
    @JsonProperty("sex")
    public sex userSex;
    private String zipCode;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersDto)) return false;
        UsersDto usersDto = (UsersDto) o;
        return getName().equals(usersDto.getName()) &&
                getUserSex() == usersDto.getUserSex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUserSex());
    }

    public UsersDto createUserDtoObject(int age, String name, UsersDto.sex sex, String zipCode) {
        UsersDto usersDto = new UsersDto();
        usersDto.setAge(age);
        usersDto.setName(name);
        usersDto.setUserSex(sex);
        usersDto.setZipCode(zipCode);
        return usersDto;
    }

    public UsersDto createUserDtoObject(String name, UsersDto.sex sex) {
        UsersDto usersDto = new UsersDto();
        usersDto.setName(name);
        usersDto.setUserSex(sex);
        return usersDto;
    }

    public static UsersDto.sex randomSex()  {
        Random random = new Random();
        List<sex> VALUES =
                List.of(UsersDto.sex.values());
        int SIZE = VALUES.size();
        return VALUES.get(random.nextInt(SIZE));
    }
}
