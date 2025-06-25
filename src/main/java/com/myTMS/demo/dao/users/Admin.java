package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;



@Entity
@DiscriminatorValue(value = "Admin")
@Getter

public class Admin extends Users {
    private String adminCode;

    private Admin(AdminUserBuilder builder) {
        super(builder);
        this.adminCode = builder.adminCode;
    }

    public Admin(){
        super();
    }

    @Override
    public UsersBuilder<?, ?> toBuilder() {
        return new AdminUserBuilder()
                .address(this.getAddress())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .password(this.getPassword())
                .userType(UserType.Admin)
                .adminCode(this.adminCode);
    }

    @Override
    public UsersBuilder<?, ?> toBuilder(Users user) {
        return new AdminUserBuilder()
                .address(user.getAddress())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .userType(UserType.Admin)
                .adminCode(this.adminCode);
    }

    @Override
    public Users updateProfile(UsersBuilder<?, ?> users) {
        this.adminCode = ((AdminUserBuilder) users).adminCode;
        return super.updateProfile(users);
    }

    public static class AdminUserBuilder extends UsersBuilder<Admin, AdminUserBuilder> {

        private String adminCode;

        public AdminUserBuilder adminCode(String adminCode) {
            this.adminCode = adminCode;
            return self();
        }

        @Override
        protected AdminUserBuilder self() {
            return this;
        }

        @Override
        public Admin build() {
            return new Admin(this);
        }
    }
}
