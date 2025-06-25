package com.myTMS.demo.dto.order;

import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.Item;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailsDTO {

    private Long orderId;
    private Long userId;
    private String userName;
    private Long totalPrice;
    private String orderDate;
    private String exceptedDate;
    private String destination;
    private String departure;
    private DeliveryType deliveryType;
    private DeliveryStatus deliveryStatus;
    private Long deliveryFee;

    private List<OrderDetailsItemListDTO> orderItems;

    public OrderDetailsDTO(Long orderId, Long userId, String userName, Long totalPrice,
                           String orderDate, String exceptedDate, String destination,
                           String departure, DeliveryType deliveryType, DeliveryStatus deliveryStatus, Long deliveryFee) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.exceptedDate = exceptedDate;
        this.destination = destination;
        this.departure = departure;
        this.deliveryType = deliveryType;
        this.deliveryStatus = deliveryStatus;
        this.deliveryFee = deliveryFee;
    }
}
