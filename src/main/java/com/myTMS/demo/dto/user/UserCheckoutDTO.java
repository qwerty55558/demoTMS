package com.myTMS.demo.dto.user;

import com.myTMS.demo.dao.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCheckoutDTO {
    @NotEmpty(message = "{NotEmpty.userCheckoutDTO.firstName}")
    private String firstName;
    @NotEmpty(message = "{NotEmpty.userCheckoutDTO.lastName}")
    private String lastName;
    @NotEmpty(message = "{NotEmpty.userCheckoutDTO.email}")
    @Email(message = "{Email.userCheckoutDTO.email}")
    private String email;
    @NotEmpty(message = "{NotEmpty.userCheckoutDTO.address}")
    private String address;
    private String address2;
    private String xAddress;
    private String yAddress;
    @Valid
    private Payment payment;

    @Override
    public String toString() {
        return "UserCheckoutDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", xAddress='" + xAddress + '\'' +
                ", yAddress='" + yAddress + '\'' +
                ", payment=" + payment +
                '}';
    }

}
