package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPAItemRepository extends JpaRepository<Item,Long> {
    Optional<Item> findByName(String name);
}
