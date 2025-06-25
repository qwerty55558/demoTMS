package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Center;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPACenterRepository extends JpaRepository<Center, Long> {
    Optional<Center> findCenterByName(String name);
}
