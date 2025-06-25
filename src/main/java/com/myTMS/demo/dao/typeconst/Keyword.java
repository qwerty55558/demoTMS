package com.myTMS.demo.dao.typeconst;

import com.myTMS.demo.dao.Center;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// SQL 인젝션 때문에 native query 방식은 반려, 대신 타입값을 줘서 keyword 마다 어떤 타입을 파라미터로 받아야하는지 체크
@Getter
public enum Keyword {
    departure(Center.class),
    userId(Long.class),
    status(DeliveryStatus.class),
    createdAt(LocalDateTime.class),
    deliveryId(Long.class),
    deliveryType(DeliveryType.class);

    private final Class<?> type;

    Keyword(Class<?> type) {
        this.type = type;
    }

}

