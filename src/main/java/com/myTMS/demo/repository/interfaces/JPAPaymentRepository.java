package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAPaymentRepository extends JpaRepository<Payment, Long> {
}
