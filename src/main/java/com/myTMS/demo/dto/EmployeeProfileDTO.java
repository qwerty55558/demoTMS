package com.myTMS.demo.dto;

import com.myTMS.demo.dao.users.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeProfileDTO {
    private String email;
    private String departmentName;
    private String firstName;
    private String lastName;

    public EmployeeProfileDTO(Employee employee) {
        this.email = employee.getEmail();
        this.departmentName = employee.getDepartment().getName();
        this.firstName = employee.getFirstName() != null ? employee.getFirstName() : "";
        this.lastName = employee.getLastName() != null ? employee.getLastName() : "";
    }
}
