package com.myTMS.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserFindPwDTO {
    @Email(message = "{Email.userFindPwDTO.email}")
    @NotBlank(message = "{NotBlank.userFindPwDTO.email}")
    private String email;
}
