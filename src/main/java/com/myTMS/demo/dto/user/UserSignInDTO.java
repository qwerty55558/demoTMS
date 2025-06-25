package com.myTMS.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSignInDTO {
    @Email(message = "{Email.userSignInDTO.email}")
    @NotBlank(message = "{NotBlank.userSignInDTO.email}")
    private String email;
    @NotBlank(message = "{NotBlank.userSignInDTO.pw}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$", message = "{Pattern.userSignInDTO.pw}")
    private String pw;
    private boolean remember;
}
