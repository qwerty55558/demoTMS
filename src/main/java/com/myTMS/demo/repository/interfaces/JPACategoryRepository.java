package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPACategoryRepository extends JpaRepository<Category, Long> {
    public Optional<Category> findByName(String name);
}
