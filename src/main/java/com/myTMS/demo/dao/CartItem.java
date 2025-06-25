package com.myTMS.demo.dao;

import lombok.Getter;
import lombok.Setter;

/**
 * 카트 내부에 존재하는 아이템을 의미하는 객체
 */
@Getter
@Setter
public class CartItem {
    private Long itemId;
    private Integer quantity;

    public CartItem(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public CartItem() {
        super();
    }
}
