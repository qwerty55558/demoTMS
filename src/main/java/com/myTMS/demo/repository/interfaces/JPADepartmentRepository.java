package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPADepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);
}
