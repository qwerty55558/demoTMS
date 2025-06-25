package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("General")
@Getter
public class General extends Users{

    private General(GeneralUserBuilder builder) {
        super(builder);
    }

    public General(){
        super();
    }

    public UsersBuilder<?, ?> toBuilder(){
        return new General.GeneralUserBuilder()
                .address(this.getAddress())
                .email(this.getEmail())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .password(this.getPassword())
                .userType(this.getUserType());
    }

    public GeneralUserBuilder toBuilder(Users user){
        return new General.GeneralUserBuilder()
                .address(user.getAddress())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .userType(UserType.General);
    }

    @Override
    public Users updateProfile(UsersBuilder<?, ?> users) {
        return super.updateProfile(users);
    }

    public static class GeneralUserBuilder extends UsersBuilder<General, GeneralUserBuilder> {

        @Override
        protected GeneralUserBuilder self() {
            return this;
        }

        @Override
        public General build() {
            return new General(this);
        }
    }
}
