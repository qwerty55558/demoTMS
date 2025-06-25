package com.myTMS.demo.dao.delivery;

import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.abstractclass.createdAtExpectedAt;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 배송 과정을 알리는 DAO 이며 orders 와 1:1 관계를 가짐
 */

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "d_type")
@DiscriminatorValue("General")
public class Delivery extends createdAtExpectedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private Long userId;
    @OneToOne(mappedBy = "delivery")
    private Orders orders;
    private String distance;
    private String destination;
    @ManyToOne
    private Center departure;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private Long price;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", userId=" + userId +
                ", orders=" + orders +
                ", distance='" + distance + '\'' +
                ", destination='" + destination + '\'' +
                ", departure=" + departure +
                ", deliveryType=" + deliveryType +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
