package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.OrderItem;
import com.myTMS.demo.dao.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JPAOrdersItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<List<OrderItem>> findOrderItemsByOrders_Id(Long orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.item WHERE oi.orders.id = :orderId")
    Optional<List<OrderItem>> findOrderItemsWithItemsByOrders_Id(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.item WHERE oi.orders IN :orders")
    List<OrderItem> findOrderItemsWithItemsByOrdersIn(@Param("orders") List<Orders> orders);

}
