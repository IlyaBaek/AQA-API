package reseponsesDTO;

import java.util.Objects;

public class UpdateUserDto {
    private UsersDto userNewValues;
    private UsersDto userToChange;

    public UpdateUserDto(UsersDto newValues, UsersDto toChange) {
        this.userNewValues = newValues;
        this.userToChange = toChange;
    }

    public UsersDto getUserNewValues() {
        return userNewValues;
    }

    public void setUserNewValues(UsersDto userNewValues) {
        this.userNewValues = userNewValues;
    }

    public UsersDto getUserToChange() {
        return userToChange;
    }

    public void setUserToChange(UsersDto userToChange) {
        this.userToChange = userToChange;
    }

    public static boolean allFieldsAreNew(UpdateUserDto updateUserDto) {
        return !(Objects.equals(updateUserDto.userNewValues.getName(), updateUserDto.userToChange.getName()) |
                updateUserDto.userNewValues.getAge().equals(updateUserDto.userToChange.getAge()) |
                Objects.equals(updateUserDto.userNewValues.getUserSex(), (updateUserDto.userToChange.getUserSex())) |
                Objects.equals(updateUserDto.userNewValues.getZipCode(), (updateUserDto.userToChange.getZipCode())));
    }
}
