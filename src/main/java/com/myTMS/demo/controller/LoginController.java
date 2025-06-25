package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.constant.StatusCode;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dto.user.UserFindPwDTO;
import com.myTMS.demo.dto.user.UserSignInDTO;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.service.*;
import com.myTMS.demo.service.norep.MailService;
import com.myTMS.demo.utils.CustomScheduler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final AlarmService alarmService;
    private final UserService userService;
    private final AuthenticationManager am;
    private final RedisService redisService;
    private final CategoryService categoryService;
    private final MailService mailService;
    private final MessageSource messageSource;
    private final LocalValidatorFactoryBean validator;
    private final CustomScheduler customScheduler;

    @Value("${alarm.max.page.size}")
    private int alarmMaxPageSize;

    /**
     * signin 의 dto 로 들어온 email 과 password 를 token 으로 생성함
     * 이전에 securityConfig 에 지정해놓은 AuthenticationManager 를 통해 token 유효성을 검증함 (CustomUserDetailService, encoder 를 통해)
     * try/catch 문을 사용하지 않은 이유는 CustumAccessDeniedHandler 에서 exception 별로 관리하기 때문
     * 에러가 발생하지 않는다면 context 에 인증 정보를 등록하고 holder 에 저장함
     * 마지막으로 세션에 인증키를 세팅해주어 세션 유지 동안 attribute 를 사용해서 contextHolder 에 인증을 진행하게 됨
     * update: 임시 비밀번호를 발급받은 유저는 검증을 거치지 않고 비밀번호 매칭만 할 수 있도록 설정
     * @param dto 검증된 데이터를 객체 매핑시켜 UserDetail 을 생성할 수 있게 함
     */
    @PostMapping(value = "/signin")
    public String loginForm(@ModelAttribute UserSignInDTO dto,BindingResult br, HttpServletRequest req, Model model) throws ServletException {
        // 수동 유효성 검사 수행
        validator.validate(dto, br); // LocalValidatorFactoryBean 사용 (Spring에서 자동 주입)

        if (br.hasErrors()) {
            String redisData = redisService.getData(dto.getEmail(), RedisConst.USER_TEMP_PASSWORD.get());
            log.info("redisData = '{}', expected = '{}'", redisData, StatusCode.OK.get());

            if (!StatusCode.OK.get().equals(redisData)) {
                model.addAttribute("errors", br.getFieldErrors());

                Optional<FieldError> any = br.getFieldErrors().stream().findAny();
                if (any.isPresent()) {
                    FieldError fieldError = any.get();
                    String objectName = fieldError.getObjectName();

                    model.addAttribute("type", objectName);

                    log.info("type = {}", objectName);
                    log.info("field = {}", fieldError.getField());
                    log.info("argument = {}", fieldError.getArguments());
                    log.info("code = {} ", fieldError.getCode());
                }
                HomeController.homeEmptyObjectInclude(model);
                return "home";
            }
        }

        try {
            // 실제 로그인 처리
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPw());
            Authentication authenticate = am.authenticate(token);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticate);
            SecurityContextHolder.setContext(context);

            HttpSession session = req.getSession();
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            redisService.deleteDataByString(dto.getEmail(), RedisConst.USER_TEMP_PASSWORD.get());

            CustomUserDetails userDetails = (CustomUserDetails) context.getAuthentication().getPrincipal();
            if (userDetails.getUserType().equals(UserType.Employee)) {
                return "redirect:/valid/employee/home";
            }
            if (userDetails.getUserType().equals(UserType.Admin)) {
                customScheduler.setDashboardChart();
                return "redirect:/valid/admin/home";
            }

            return "redirect:/valid/home";

        } catch (AuthenticationException e) {
            throw new ServletException(e);
        }
    }

    /**
     *  사용자 가입 요청에 사용되는 엔트포인트
     *  dto 로 받아온 정보로 redis cache 데이터에 이메일이 정말 인증되었는지 확인하고 userService 를 통한 가입이 허가되었는지 확인
     *  둘 다 true 일 경우 성공, 실패할 경우는 이미 emailController 에서 처리하기 때문에 처리하지 않음.
     *  home 으로 보내졌을 때 thymeleaf 의 object 를 사용할 경우 폼에 비어있는 객체를 로딩하지 않으면 에러가 생겨
     *  homeController 에 static method 를 사용해 빈 폼을 채워줌
     */
    @PostMapping(value = "/signup")
    public String registration(@ModelAttribute @Valid UserSignUpDTO dto, Model model) {
        log.info("signup = {}", dto.toString());

        if (userService.signUpUser(dto) && redisService.getData(dto.getEmail(), RedisConst.AUTH.get("userSignUpDTO")).equals(StatusCode.OK.get())) {
            log.info("성공");
            redisService.deleteDataByString(dto.getEmail(),RedisConst.AUTH.get("userSignUpDTO"));
            redisService.deleteDataByString(dto.getEmail(),RedisConst.EMAIL.get("userSignUpDTO"));
        } else {
            log.info("실패");
        }
        HomeController.homeEmptyObjectInclude(model);
        return "home";
    }

    /**
     * #TODO
     */
//    @PostMapping(value = "/signup/employee")
//    public String registrationEmployee(@ModelAttribute @Valid UserSignUpDTO dto, Model model) {
//        log.info("signup = {}", dto.toString());
//
//        if (userService.signUpUser(dto) && redisService.getData(dto.getEmail(), RedisConst.AUTH.get("userSignUpDTO")).equals(StatusCode.OK.get())) {
//            log.info("성공");
//            redisService.deleteDataByString(dto.getEmail(),RedisConst.AUTH.get("userSignUpDTO"));
//            redisService.deleteDataByString(dto.getEmail(),RedisConst.EMAIL.get("userSignUpDTO"));
//        } else {
//            log.info("실패");
//        }
//        HomeController.homeEmptyObjectInclude(model);
//        return "home";
//    }

    /**
     * 사용자 비밀번호 찾기 요청에 사용되는 엔드포인트
     * 요청을 할 시에는 이메일 인증 상태를 확인하고 redis 를 초기화 한 다음 mailService 를 통해서 임시 비밀번호를 발급받을 수 있음
     * 그리고 Valid 를 패스할 수 있는 권한을 얻게됨, 권한은 signIn 에서 로그인 시에 권한을 삭제함
     */

    @PostMapping(value = "/findpw")
    public String findPassWord(@ModelAttribute @Valid UserFindPwDTO dto, HttpServletRequest req, Model model) {

        log.info("findpw = {}", dto.toString());
        if (redisService.getData(dto.getEmail(), RedisConst.AUTH.get("userFindPwDTO")).equals(StatusCode.OK.get())) {
            log.info("성공");
            redisService.deleteDataByString(dto.getEmail(), RedisConst.AUTH.get("userFindPwDTO"));
            redisService.deleteDataByString(dto.getEmail(), RedisConst.EMAIL.get("userFindPwDTO"));
            userService.findByEmail(dto.getEmail()).ifPresent(user -> {
                String tempPw = userService.changePwUsers(user);
                mailService.send(
                        mailService.buildPwResetTemplate(dto.getEmail(), req.getLocale(), tempPw)
                );
            });
            redisService.setStringData(dto.getEmail(), RedisConst.USER_TEMP_PASSWORD.get(), StatusCode.OK.get());
        } else {
            log.info("실패");
        }
        HomeController.homeEmptyObjectInclude(model);
        return "home";
    }


    /**
     * 인증 유저의 홈 화면으로  redis 에 저장한 카테고리를 불러오고 UserType 을 model 에 담아 전송함
     */
    @GetMapping(value = "/valid/home")
    public String validUserPermit(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("userType",  userDetails.getUserType().toString());
        model.addAttribute("alarms", alarmService.getAlarmsForGeneral(userDetails.getUserId(), 0, alarmMaxPageSize));
        return "valid/home";
    }

    /**
     * GET 방식의 폼 로그아웃을 제거하고 POST 방식으로 로그아웃 서비스를 구현
     * fetchAPI 를 통해서 로그아웃을 요청할 경우 session 을 invalidate 시키고 securityContextHolder 에서도 clearContext 를 호출
     * @param request
     * @return
     */
    @PostMapping(value = "/logout")  // GET 대신 POST로 변경
    @ResponseBody  // Ajax 응답을 위해 추가
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("/home");  // 리다이렉트할 URL 반환
    }

    /**
     * 인증 과정에서 field error 가 발생할 경우에 binding result 를 통한 에러 처리를 진행함
     * 해당 클래스 내부의 인증 에러만 관여하는 국소적인 에러처리메서드
     * 일단 에러들은 errors 를 통해 model 에 등록하고
     * type 을 알기 위해서 (form 이 3개가 존재하기 때문에 Carousel 로 구현한 슬라이딩 페이지의 시작 지점을 설정해야함) 따로 type 데이터를 model 에 등록함
     * (어짜피 submit 을 보냈다는 것은 하나의 form 에서만 전송을 한 것이기 때문에 어떤 에러든지 발생했다면 type 을 지정할 수 있음)
     * 추가적으로 type 을 명시적으로 등록함으로써 프론트 단에서 쉽게 이해 가능
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model, HttpServletRequest req, HttpServletResponse resp) {
        BindingResult br = ex.getBindingResult();
        model.addAttribute("errors", br.getFieldErrors());

        Optional<FieldError> any = br.getFieldErrors().stream().findAny();
        if (any.isPresent()) {
            FieldError fieldError = any.get();
            String objectName = fieldError.getObjectName();

            model.addAttribute("type", objectName);

            log.info("type = {}", objectName);
            log.info("field = {}",fieldError.getField());
            log.info("argument = {}",fieldError.getArguments());
            log.info("code = {} ",fieldError.getCode());
        }

        HomeController.homeEmptyObjectInclude(model);
        return "home";
    }

}
