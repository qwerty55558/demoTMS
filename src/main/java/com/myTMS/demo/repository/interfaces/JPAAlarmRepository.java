package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JPAAlarmRepository extends JpaRepository<Alarm, Long> {
    @Query("SELECT a FROM Alarm a WHERE a.userId = :userId OR a.userId = -1 ORDER BY a.createdAt DESC")
    Page<Alarm> findForUserOrGlobal(@Param("userId") Long userId, Pageable pageable);

    Page<Alarm> findPageByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteAlarmsByUserId(Long userId);
}
