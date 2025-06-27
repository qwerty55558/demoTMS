package com.myTMS.demo.service;

import com.myTMS.demo.dao.Department;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.users.*;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.repository.interfaces.JPADepartmentRepository;
import com.myTMS.demo.repository.interfaces.UserRepository;
import com.myTMS.demo.utils.RandomAuthCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JPADepartmentRepository departmentRepository;

    public void logoutUser(Long userId) {
        userRepository.findUserById(userId).ifPresent(u -> {
            u.setLastCheckedAt(LocalDateTime.now());
        });
    }

    public boolean saveUser(Users users) {
        return userRepository.saveUser(users);
    }

    public boolean signUpUser(UserSignUpDTO dto) {
        Optional<Users> userByEmail = userRepository.findUserByEmail(dto.getEmail());
        if (userByEmail.isEmpty()) {
            String encoded = encoder.encode(dto.getPw());
            Users user = new General.GeneralUserBuilder()
                    .email(dto.getEmail())
                    .password(encoded)
                    .build();
            userRepository.createUser(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean signUpEmployee(UserSignUpDTO dto, Department department) {
        Optional<Users> userByEmail = userRepository.findUserByEmail(dto.getEmail());
        if (userByEmail.isEmpty()) {
            String encoded = encoder.encode(dto.getPw());
            Users build = new Employee.EmployeeUserBuilder()
                    .email(dto.getEmail())
                    .password(encoded)
                    .Department(department)
                    .build();
            userRepository.createUser(build);
            return true;
        } else {
            return false;
        }
    }

    public boolean signUpAdmin(UserSignUpDTO dto){
        Optional<Users> userByEmail = userRepository.findUserByEmail(dto.getEmail());
        if (userByEmail.isEmpty()) {
            String encode = encoder.encode(dto.getPw());
            Admin build = new Admin.AdminUserBuilder()
                    .email(dto.getEmail())
                    .password(encode)
                    .build();
            userRepository.createUser(build);
            return true;
        }else {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Users> findById(Long id) {
        return userRepository.findUserById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Users> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Users> findByEmailWithOrders(String email) {
        return userRepository.findUserByEmailWithOrders(email);
    }

    @Transactional(readOnly = true)
    public Boolean checkDuplicatedUser(String email) {
        return userRepository.isDuplicatedByEmail(email);
    }

    public Optional<Users> updateUser(Users user, UserType userType, String attributeValue) {
        switch (userType) {
            case Delivery -> {
                PFS updatedUser = new PFS.PFSUserBuilder().build()
                        .toBuilder(user)
                        .PCC(attributeValue)
                        .build();
                userRepository.updateUser(updatedUser);
                break;
            }
            case Business -> {
                Business updatedUser = new Business.BusinessUserBuilder().build()
                        .toBuilder(user)
                        .BRN(attributeValue)
                        .build();
                userRepository.updateUser(updatedUser);
                break;
            }
            case General -> {
                General updatedUser = new General.GeneralUserBuilder().build()
                        .toBuilder(user)
                        .build();
                userRepository.updateUser(updatedUser);
                break;
            }
        }
        return Optional.of(user);
    }

    public String changePwUsers(Users user) {
        String generate = RandomAuthCodeGenerator.generate();
        user.updateProfile(user.toBuilder(user)
                .password(encoder.encode(generate)));
        return generate;
    }


    public void setDepartment(Employee employee, String departmentName) {
        departmentRepository.findByName(departmentName).ifPresent(department -> {
            userRepository.updateUser(employee.toBuilder().Department(department).build());
        });
    }

    @Transactional(readOnly = true)
    public String getUsersCount() {
        Long usersCount = userRepository.getUsersCount();
        if (usersCount != -1L) {
            return String.valueOf(userRepository.getUsersCount());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, Long> userSignUpTrendLast5Days(){
        return userRepository.getUserSignUpTrendLast5Days();
    }

}
