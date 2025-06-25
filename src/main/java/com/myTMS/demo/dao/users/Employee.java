package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.Department;
import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("Employee")
public class Employee extends Users{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    private Employee (EmployeeUserBuilder builder){
        super(builder);
        this.department = builder.department;
    }

    public EmployeeUserBuilder toBuilder(Users user){
        return new EmployeeUserBuilder()
                .address(user.getAddress())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .Department(this.department);
    }

    public EmployeeUserBuilder toBuilder(){
        return new EmployeeUserBuilder()
                .address(this.getAddress())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .password(this.getPassword())
                .Department(this.department)
                .userType(UserType.Employee);
    }

    @Override
    public Users updateProfile(UsersBuilder<?, ?> users) {
        this.department = ((EmployeeUserBuilder) users).department;
        return super.updateProfile(users);
    }

    public Employee(){
        super();
    }

    public static class EmployeeUserBuilder extends UsersBuilder<Employee, EmployeeUserBuilder> {
        private Department department;

        public EmployeeUserBuilder Department(Department department) {
            this.department = department;
            return self();
        }

        @Override
        protected EmployeeUserBuilder self() {
            return this;
        }

        @Override
        public Employee build() {
            return new Employee(this);
        }
    }


}
