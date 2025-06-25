package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.OrderItem;
import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import com.myTMS.demo.dto.OrderCountProjection;
import com.myTMS.demo.dto.order.OrderDetailsDTO;
import com.myTMS.demo.dto.order.OrderDetailsItemListDTO;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JPAOrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findOrdersByUsers_Id(Long userId);

    @Query("SELECT new com.myTMS.demo.dto.order.OrderDetailsDTO(" +
            "o.id, u.id, u.email, o.totalPrice, " +
            "CAST(o.createdAt AS string), CAST(d.expectedAt AS string ), " +
            "d.destination, d.departure.name, d.deliveryType, d.status, d.price) " +
            "FROM Orders o " +
            "JOIN o.users u " +
            "JOIN o.delivery d " +
            "WHERE o.id = :orderId")
    Optional<OrderDetailsDTO> findOrderDetails(@Param("orderId") Long orderId);

    @Query("SELECT FUNCTION('DATE', o.createdAt) AS date, COUNT(o) AS count " +
            "FROM Orders o " +
            "WHERE o.createdAt >= :startDate " +
            "GROUP BY FUNCTION('DATE', o.createdAt) " +
            "ORDER BY FUNCTION('DATE', o.createdAt)")
    List<OrderCountProjection> countOrdersByDate(@Param("startDate") LocalDateTime startDate);


    Page<Orders> findByDelivery_Departure_Id(Long id, Pageable pageable);

    Page<Orders> findByDelivery_Id(Long id,
                                   Pageable pageable);
    Page<Orders> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
    Page<Orders> findByDelivery_DeliveryType(DeliveryType deliveryType, Pageable pageable);
    Page<Orders> findByDelivery_Status(DeliveryStatus deliveryStatus, Pageable pageable);
    Page<Orders> findByDelivery_UserId(Long userId, Pageable pageable);

    Long countByDelivery_StatusNot(DeliveryStatus deliveryStatus);
}
