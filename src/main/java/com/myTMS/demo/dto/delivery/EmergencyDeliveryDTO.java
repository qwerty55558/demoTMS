package com.myTMS.demo.dto.delivery;

import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import lombok.Getter;

@Getter
public class EmergencyDeliveryDTO {
    private Long deliveryId;
    private Long orderId;
    private String distance;
    private String destination;
    private String centerName;
    private DeliveryType deliveryType;
    private DeliveryStatus deliveryStatus;

    public EmergencyDeliveryDTO(Delivery delivery) {
        this.deliveryId = delivery.getId();
        this.orderId = delivery.getOrders().getId();
        this.distance = delivery.getDistance();
        this.destination = delivery.getDestination();
        this.centerName = delivery.getDeparture().getName();
        this.deliveryType = delivery.getDeliveryType();
        this.deliveryStatus = delivery.getStatus();
    }
}
