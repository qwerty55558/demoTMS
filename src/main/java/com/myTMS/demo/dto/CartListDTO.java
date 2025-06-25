package com.myTMS.demo.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartListDTO {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    private String imgSrc;
    private Integer price;
    private String description;
    private Integer quantity;

    public CartListDTO(Long id, String name, String imgSrc, String description, Integer price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.imgSrc = imgSrc;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
}