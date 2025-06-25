package com.myTMS.demo.config.security;

import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

    /**
     * UserDetails 를 생성해주는 UserDetailsService 를 상속받은 구현체
     * UserService 를 주입받아 실제 DB의 데이터와 매핑한다.
     * Password 는 이미 Encoding 상태로 저장되어서 그냥 UserDetails 에 등록만 하면 됨.
     * @param username : AuthenticationManager 가 인증할 때 사용하는 파라미터 email 정보를 담고있음
     * @return : 빌더패턴으로 UserDetails 를 return
     * @throws UsernameNotFoundException : CustomAuthenticationEntryPoint 의 에러처리로 핸들링해놓음.
     * BadCredentialsException 과 구별되지 않게 처리하는 것이 관건
     * 
     * update: 기존 User 를 Builder 패턴으로 생성하는 방식에서 생성자 메서드를 사용해 CustomUserDetails 를 생성하는 방식으로 수정
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> byEmail = service.findByEmail(username);
        if (byEmail.isPresent()) {
            Users users = byEmail.get();
            log.info("users = {}", users);
            return new CustomUserDetails(users);
//            return User.builder()
//                    .username(users.getEmail())
//                    .roles(users.getUserType().name())
//                    .password(users.getPassword())
//                    .build();
        }
        return null;
    }
}

