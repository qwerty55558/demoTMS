package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.users.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


/**
 * 사용자의 주문을 의미하는 객체
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"users", "orderItems", "delivery"})
public class Orders extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;
    @OneToMany(mappedBy = "orders")
    private List<OrderItem> orderItems;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    private Long totalPrice;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
