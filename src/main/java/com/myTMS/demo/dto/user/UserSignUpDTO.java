package com.myTMS.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSignUpDTO {
    @Email(message = "{Email.userSignUpDTO.email}")
    @NotBlank(message = "{NotBlank.userSignUpDTO.email}")
    private String email;
    @NotBlank(message = "{NotBlank.userSignUpDTO.pw}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",message = "{Pattern.userSignUpDTO.pw}")
    private String pw;
}
