package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("Business")
@Getter
public class Business extends Users{
    private String BRN;

    private Business(BusinessUserBuilder builder) {
        super(builder);
        this.BRN = builder.BRN;
    }
    public Business(){
        super();
    }

    public BusinessUserBuilder toBuilder(Users users) {
        return new BusinessUserBuilder()
                .address(users.getAddress())
                .email(users.getEmail())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .password(users.getPassword())
                .userType(UserType.Business)
                .BRN(this.BRN);
    }

    public BusinessUserBuilder toBuilder(){
        return new BusinessUserBuilder()
                .address(this.getAddress())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .password(this.getPassword())
                .userType(UserType.Business)
                .BRN(this.BRN);
    }

    @Override
    public Users updateProfile(UsersBuilder<?, ?> users) {
        this.BRN = ((BusinessUserBuilder) users).BRN;
        return super.updateProfile(users);
    }

    public static class BusinessUserBuilder extends UsersBuilder<Business, BusinessUserBuilder>{

        private String BRN;

        public BusinessUserBuilder BRN(String BRN) {
            this.BRN = BRN;
            return self();
        }

        public BusinessUserBuilder setBRN(String BRN) {
            this.BRN = BRN;
            return self();
        }

        @Override
        protected BusinessUserBuilder self() {
            return this;
        }

        @Override
        public Business build() {
            return new Business(this);
        }
    }
}
