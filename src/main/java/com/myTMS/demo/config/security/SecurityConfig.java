package com.myTMS.demo.config.security;

import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.handler.CustomAccessDeniedHandler;
import com.myTMS.demo.handler.CustomAuthenticationEntryPoint;
import com.myTMS.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final UserService userService;
    private final MessageSource messageSource;

    /**
     * filterChain 사이에 Proxy 되어 들어가는 체이닝 메서드로 알고있음.
     * 개발 과정이라 csrf, cors 는 disable 해놓았음
     * 비회원이 접근할 수 있는 루트들은 permitAll
     * 에러 핸들링도 커스텀하여 등록
     * update: 권한에 따른 접근 제한을 추가 (Employee)
     * @param http
     * @return
     * @throws Exception
     */
    
    // #TODO `/api` 등 접근 권한에대한 설정이 필요함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/info/**","/error/**", "/signin", "/signup", "/findpw", "api/email/**", "/home", "/", "/asset/**", "/css/**", "/js/**", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/valid/employee/**","/sse/employee/**").hasAuthority(UserType.Employee.name())
                        .requestMatchers("/sse/general").hasAuthority(UserType.General.name())
                        .requestMatchers("/valid/admin/**").hasAuthority(UserType.Admin.name())
                        .anyRequest().authenticated())
//                        .anyRequest().permitAll())
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint()));
        return http.build();
    }

    /**
     * 객체 지향적으로 인증을 진행하기 위해 SecurityFilterChain 에서 formLogin 을 사용하지 않고 따로 manager 를 구현함
     * 커스텀한 UserDetailsService 를 등록
     * Encoder 의 경우는 해시값을 비교하기 위해서 등록
     * @return : 인증 방식에 따라 달라지는 provider 를 핸들링 하기 위해 manager 를 리턴하는 것 같음 (AuthenticationProviders 를 지원하기 위한 Manager 고
     * 이번 프로젝트에서는 user/password 베이스의 DaoAuthenticationProvider 를 사용함)
     */
    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return new ProviderManager(provider);
    }


    /**
     * 단순 빈 등록
     * 사용자 세션 데이터 저장을 구현한 CustomUserDetailService 를 빈 등록
     * 인증되지 않은 유저 즉, 시큐리티 필터 체인 단계에서 허용되지 않은 유저를 denied 처리해주는 핸들러를 빈 등록
     * 각종 인증 단계에서 생기는 에러를 타입에 따라 분류해 메시징 해주는 클래스를 빈 등록 
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(messageSource);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint(messageSource);
    }
}