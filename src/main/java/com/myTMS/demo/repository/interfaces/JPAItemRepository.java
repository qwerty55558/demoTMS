package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JPAItemRepository extends JpaRepository<Item,Long> {
    Optional<Item> findByName(String name);
}
