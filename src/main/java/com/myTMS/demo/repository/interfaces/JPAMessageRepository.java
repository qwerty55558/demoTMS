package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAMessageRepository extends JpaRepository<Message, Long> {
}
