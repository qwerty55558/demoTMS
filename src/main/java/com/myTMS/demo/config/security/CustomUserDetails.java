package com.myTMS.demo.config.security;

import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.users.Users;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 * User 클래스 (Spring Security 에서 제공하는 사용자 인증 컨텍스트) 를 상속받고 필드를 추가해주는 커스텀 클래스
 * UserId 와 UserType 을 추가 필드로 가짐 (세션 데이터에 저장함으로써 각 로직에서 유저 데이터를 쉽게 얻어올 수 있음)
 */

public class CustomUserDetails extends User {

    private final Long userId;
    private final UserType userType;

    public CustomUserDetails(Users users) {
        super(users.getEmail(), users.getPassword(), AuthorityUtils.createAuthorityList(users.getUserType().name()));
        this.userId = users.getId();
        this.userType = users.getUserType();
    }

    public Long getUserId() {
        return userId;
    }
    public UserType getUserType(){return userType;}



}
