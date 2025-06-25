package com.myTMS.demo.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 카테고리에 속하는 아이템
 */
@Entity
@Setter
@Getter
public class CategoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


}
