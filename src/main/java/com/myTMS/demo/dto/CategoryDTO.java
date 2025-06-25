package com.myTMS.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer depth;

    public CategoryDTO(Long id, String name, Long parentId, Integer depth) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.depth = depth;
    }

    public CategoryDTO() {
    }
}