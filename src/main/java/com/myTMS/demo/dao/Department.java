package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.users.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Department extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Employee> employeeList = new ArrayList<>();

}
