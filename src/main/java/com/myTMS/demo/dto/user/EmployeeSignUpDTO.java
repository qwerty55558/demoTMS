package com.myTMS.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeSignUpDTO {
    @Email(message = "{Email.userSignUpDTO.email}")
    @NotBlank(message = "{NotBlank.userSignUpDTO.email}")
    private String email;
}
