package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.delivery.Delivery;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 각 지부에 위치하는 센터의 위치정보를 담은 객체이다
 * 이 객체를 이용해 출발지를 지정하거나 담당 센터를 지정해준다.
 */
@Entity
@Getter
public class Center extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;

    public Center() {
    }

    private Double xAddress;
    private Double yAddress;
    private String description;
    @OneToMany(mappedBy = "departure")
    private List<Delivery> deliveries;

    @Builder
    public Center(String name, Double xAddress, Double yAddress, String description) {
        this.name= name;
        this.xAddress = xAddress;
        this.yAddress = yAddress;
        this.description = description;
        deliveries = new ArrayList<>();
    }
}
