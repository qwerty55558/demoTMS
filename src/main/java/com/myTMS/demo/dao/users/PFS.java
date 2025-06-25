package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("Delivery")
public class PFS extends Users{
    private String PCC;

    private PFS(PFSUserBuilder builder) {
        super(builder);
        this.PCC = builder.PCC;
    }

    public PFS(){
        super();
    }

    public PFSUserBuilder toBuilder(Users user){
        return new PFSUserBuilder()
                .address(user.getAddress())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .userType(UserType.Delivery)
                .PCC(this.PCC);
    }

    public PFSUserBuilder toBuilder(){
        return new PFSUserBuilder()
                .address(this.getAddress())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .password(this.getPassword())
                .userType(UserType.Delivery)
                .PCC(this.PCC);
    }

    @Override
    public Users updateProfile(UsersBuilder<?, ?> users) {
        this.PCC = ((PFSUserBuilder) users).PCC;
        return super.updateProfile(users);
    }

    public static class PFSUserBuilder extends UsersBuilder<PFS, PFSUserBuilder>{
        private String PCC;

        public PFSUserBuilder PCC(String PCC) {
            this.PCC = PCC;
            return self();
        }

        @Override
        protected PFSUserBuilder self() {
            return this;
        }

        @Override
        public PFS build() {
            return new PFS(this);
        }
    }
}
