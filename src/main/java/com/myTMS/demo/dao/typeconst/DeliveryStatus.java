package com.myTMS.demo.dao.typeconst;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    OrderPlacement,
    Processing,
    Packaging,
    Shipping,
    InTransit,
    OutForDelivery,
    Delivered;

    public static DeliveryStatus nextStatus(DeliveryStatus currentStatus){
        int i = currentStatus.ordinal() + 1;
        if (i >= DeliveryStatus.values().length) {
            return DeliveryStatus.Delivered;
        }else {
            return DeliveryStatus.values()[i];
        }
    }
}

