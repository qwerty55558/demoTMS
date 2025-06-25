package com.myTMS.demo.dao.users;

import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.abstractclass.createdAtLastCheckedAt;
import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 모든  Users 클래스의 추상 클래스 + 부모
 * 제너럴 빌더 패턴을 사용해 자식 클래스도 부모 내용을 포함하여 쉽고 명시적인 객체 생성이 가능
 * 수정할 부분이 있는 객체의 경우에는 toBuilder 를 사용하여 수정이 가능하며
 * DB 에 JPA 가 구현해줄 상속의 부분에서는 JOINED 전략을 사용함
 * Order 의 경우에는 users 가 사라지면 관련된 row 들은 다 삭제되도록 구현
 * userUpdate 할 때 orders 가 지연 로딩이 되어서 문제가 생길 수 있지만
 * UserService 단계에서 ***WithOrders 를 통해 즉시 fetch 하도록 만들어 디폴트 성능과 로직의 다양성을 확보
 */

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "userType")
@ToString(exclude = {"orders"})
public abstract class Users extends createdAtLastCheckedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "userType", insertable = false, updatable = false)
    private UserType userType;
    @OneToMany(mappedBy = "users",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Orders> orders = new ArrayList<>();

    protected Users(UsersBuilder<?, ?> builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.address = builder.address;
        this.password = builder.password;
        this.userType = builder.userType;
        this.email = builder.email;
        orders.addAll(builder.orders);
    }

    public Users(){

    }

    public abstract UsersBuilder<?, ?> toBuilder();
    public abstract UsersBuilder<?, ?> toBuilder(Users user);

    public Users updateProfile(UsersBuilder<?, ?> users) {
        this.firstName = users.firstName;
        this.lastName = users.lastName;
        this.password = users.password;
        return this;
    }

    public static abstract class UsersBuilder<T extends Users, B extends UsersBuilder<T, B>> {
        private String password;
        private String firstName;
        private String lastName;
        private String address;
        private String email;
        private UserType userType;
        private List<Orders> orders = new ArrayList<>();

        public B firstName(String firstName){
            this.firstName = firstName;
            return self();
        }
        public B lastName(String lastName){
            this.lastName = lastName;
            return self();
        }
        public B password(String password){
            this.password = password;
            return self();
        }
        public B email(String email){
            this.email = email;
            return self();
        }
        public B userType(UserType userType){
            this.userType = userType;
            return self();
        }
        public B orders(Orders orders){
            this.orders.add(orders);
            return self();
        }
        public B orders(List<Orders> orders){
            this.orders.addAll(orders);
            return self();
        }

        public B address(String address) {
            this.address = address;
            return self();
        }

        protected abstract B self();
        public abstract T build();
    }
}
