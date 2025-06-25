package com.myTMS.demo.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myTMS.demo.dao.abstractclass.createdAtLastCheckedAt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 아이템의 상세 정보를 담은 객체
 */
@Entity
@Setter
@Getter
public class Item extends createdAtLastCheckedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imgSrc;

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<CategoryItem> category;
}
