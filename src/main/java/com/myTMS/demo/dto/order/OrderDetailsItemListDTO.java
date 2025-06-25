package com.myTMS.demo.dto.order;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailsItemListDTO {
    private Long Id;
    private String name;
    private String imgSrc;
    private String description;
    private Integer price;
    private Integer amount;

    public OrderDetailsItemListDTO() {
    }

    public OrderDetailsItemListDTO(Long id, String name, String imgSrc, String description, Integer price, Integer amount) {
        Id = id;
        this.name = name;
        this.imgSrc = imgSrc;
        this.description = description;
        this.price = price;
        this.amount = amount;
    }
}
