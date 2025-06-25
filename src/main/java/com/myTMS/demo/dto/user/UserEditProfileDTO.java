package com.myTMS.demo.dto.user;

import com.myTMS.demo.annotation.OptionalPattern;
import com.myTMS.demo.dao.typeconst.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditProfileDTO {
    private String email;
    @OptionalPattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
            message = "{Pattern.userSignUpDTO.pw}")
    private String password;
    private String firstName;
    private String lastName;
    private String attributeName;
    private String attributeValue;
    private UserType userType;
    private String createdAt;

    @Override
    public String toString() {
        return "UserEditProfileDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", attributevalue='" + attributeValue + '\'' +
                ", userType=" + userType +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

}
