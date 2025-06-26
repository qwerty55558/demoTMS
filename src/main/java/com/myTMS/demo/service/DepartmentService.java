package com.myTMS.demo.service;

import com.myTMS.demo.dao.Department;
import com.myTMS.demo.dto.EmployeeProfileDTO;
import com.myTMS.demo.repository.interfaces.JPADepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final JPADepartmentRepository repository;

    public void createDepartment(String name) {
        if (repository.findByName(name).isEmpty()) {
            Department department = new Department();
            department.setName(name);
            repository.save(department);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Department> findDepartmentByName(String name) {
        return repository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Map<String, List<EmployeeProfileDTO>> cacheDepartment() {
        Map<String, List<EmployeeProfileDTO>> result = new HashMap<>();

        List<Department> all = repository.findAll();
        for (Department department : all) {
            List<EmployeeProfileDTO> lists = new ArrayList<>();
            department.getEmployeeList().forEach(employee -> {
                lists.add(new EmployeeProfileDTO(employee));
            });
            result.put(department.getName(), lists);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<String> findDepartmentNames(){
        return repository.findAll().stream()
                .map(Department::getName)
                .toList();
    }

}
