package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPACategoryItemRepository extends JpaRepository<CategoryItem, Long> {
    public Optional<CategoryItem> findByItemName(String itemName);
}
