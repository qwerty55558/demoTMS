package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문에 존재하는 아이템을 다대다 형태로 매핑시키기 위해 존재하는
 * 매핑 엔티티
 */
@Entity
@Setter
@Getter
public class OrderItem extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;
    private Integer orderPrice;
    private Integer amount;
}
