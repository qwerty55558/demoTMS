package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.users.Users;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    Optional<Users> findUserById(Long id);

    Optional<Users> findUserByEmail(String name);

    Optional<Users> findUserByEmailWithOrders(String name);

    Long getUsersCount();

    Map<LocalDate, Long> getUserSignUpTrendLast5Days();



    boolean updateUser(Users user);

    boolean createUser(Users user);

    boolean saveUser(Users user);

    Boolean isDuplicatedByEmail(String email);

}
