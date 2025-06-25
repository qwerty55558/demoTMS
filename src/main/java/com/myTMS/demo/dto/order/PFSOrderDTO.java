package com.myTMS.demo.dto.order;

import com.myTMS.demo.dao.typeconst.WeightCategory;
import com.myTMS.demo.dto.user.UserCheckoutDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PFSOrderDTO {
    @Valid
    private UserCheckoutDTO DTO;
    @NotNull(message = "{NotEmpty.PFSOrderDTO.weightCategory}")
    private WeightCategory weightCategory;
    @NotEmpty(message = "{NotEmpty.PFSOrderDTO.trackingNumber}")
    private String trackingNumber;

    public PFSOrderDTO(String email) {
        UserCheckoutDTO userCheckoutDTO = new UserCheckoutDTO();
        userCheckoutDTO.setEmail(email);
        this.DTO = userCheckoutDTO;
    }

    public PFSOrderDTO() {
    }

    @Override
    public String toString() {
        return "PFSOrderDTO{" +
                "DTO=" + DTO.toString() +
                ", weightCategory=" + weightCategory +
                ", trackingNumber='" + trackingNumber + '\'' +
                '}';
    }
}
