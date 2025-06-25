package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dto.CenterDeliveryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JPADeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByExpectedAtLessThanAndStatusNot(Date expectedAt, DeliveryStatus status);

    @Query("SELECT d.departure AS center, COUNT(d) AS count " +
            "FROM Delivery d " +
            "WHERE d.expectedAt > :expectedAt AND d.status <> :status " +
            "GROUP BY d.departure")
    List<CenterDeliveryCount> countDeliveriesByCenterBeforeExpectedAtAndStatusNot(
            @Param("expectedAt") Date expectedAt,
            @Param("status") DeliveryStatus status
    );
}
