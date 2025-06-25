package com.myTMS.demo.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemListDTO {
    private Long orderId;
    private String itemName;
    private String itemImgSrc;
    private Integer cartSize;
    private Long orderPrice;

    public OrderItemListDTO(Long orderId, String itemName, String itemImgSrc, Integer cartSize, Long orderPrice) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.itemImgSrc = itemImgSrc;
        this.cartSize = cartSize;
        this.orderPrice = orderPrice;
    }
    public OrderItemListDTO(Long orderId, String itemName, String itemImgSrc, Long cartSize, Long orderPrice) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.itemImgSrc = itemImgSrc;
        this.cartSize = Math.toIntExact(cartSize);
        this.orderPrice = orderPrice;
    }

    public OrderItemListDTO() {
    }
}
