package reseponsesDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UsersDto {
    private int age = RandomUtils.nextInt(1, 99);
    private String name = RandomStringUtils.random(5, true, false);
    @JsonProperty("sex")
    public Sex userSex = UsersDto.Sex.getRandomSex();
    private String zipCode;

    public enum Sex {
        FEMALE, MALE;

        public static Sex getRandomSex() {
            Random random = new Random();
            List<Sex> VALUES =
                    List.of(Sex.values());
            int SIZE = VALUES.size();
            return VALUES.get(random.nextInt(SIZE));
        }
    }

    public UsersDto() {
    }

    public UsersDto(String nameOfUser, Sex sexOfUser) {
        name = nameOfUser;
        userSex = sexOfUser;
    }

    public void setUserSex(Sex input) {
        userSex = input;
    }

    public Sex getUserSex() {
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
        if (getName() == null || getUserSex() == null) return false;
        return getName().equals(usersDto.getName()) &&
                getUserSex() == usersDto.getUserSex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUserSex());
    }
}
