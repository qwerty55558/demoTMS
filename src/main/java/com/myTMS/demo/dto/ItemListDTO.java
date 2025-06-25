package com.myTMS.demo.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemListDTO {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    private String imgSrc;
    private Integer price;
    private Long categoryId;
    private String description;

    public ItemListDTO(Long id, String name, String imgSrc, Long categoryId, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.imgSrc = imgSrc;
        this.categoryId = categoryId;
        this.description = description;
        this.price = price;
    }
}
