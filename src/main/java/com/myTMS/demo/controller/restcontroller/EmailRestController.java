package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.constant.StatusCode;
import com.myTMS.demo.dto.MailTemplate;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.UserService;
import com.myTMS.demo.service.norep.MailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/api")
public class EmailRestController {

    // 이메일 형식 확인을 위한 정규 표현식
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final MailService mailService;
    private final MessageSource ms;
    private final RedisService redisService;
    private final UserService userService;

    public EmailRestController(MailService mailService, MessageSource ms, RedisService redisService, UserService userService) {
        this.mailService = mailService;
        this.ms = ms;
        this.redisService = redisService;
        this.userService = userService;

    }


    /**
     * fetch API 요청이 오면 map 으로 데이터를 받고
     * pattern 분석, type 분석(signup 의 경우에는 email 이 겹치면 안되기 때문) 하여서 조건 충족 X 시에는 국제화된 메시지 전송
     * MailTemplate 빌더 패턴을 사용하여 title, 인증번호, 내용 등등 을 구성하여 template 지정 후 MailService 를 통해 인증메일 전송
     * redisService 에 상수를 통한 임시 데이터 저장으로 인증 번호 확인 (opsForHash 로 구현하여 key + Map(hashKey + value) 으로 구성됨)
     * 
     * update: userFindPwDTO 를 추가함
     * @param data : 파싱 데이터
     * @return 분기에 따라 데이터를 보냄
     */
    @PostMapping("/email/valid")
    public ResponseEntity<String> sendEmail(@RequestBody HashMap<String, String> data, HttpServletRequest req) {

        String email = data.get("email");
        String type = data.get("type");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ResponseEntity<>(ms.getMessage("Email.userSignInDTO.email", null, req.getLocale()), HttpStatus.NOT_FOUND);
        }
        if (type.equals("userSignUpDTO")) {
            if (userService.checkDuplicatedUser(email)) {
                return new ResponseEntity<>(ms.getMessage("duplicated.email", null, req.getLocale()), HttpStatus.NOT_FOUND);
            }
        }

        if (type.equals("userFindPwDTO")) {
            if (!userService.checkDuplicatedUser(email)) {
                return new ResponseEntity<>(ms.getMessage("not.found.email", null, req.getLocale()), HttpStatus.NOT_FOUND);
            }
        }

        try {
            MailTemplate mailTemplate = mailService.buildAuthTemplate(email, req.getLocale(), type);
            mailService.send(mailTemplate);
        } catch (Exception e) {
            log.info("Auth Mail Sending Service Error", e);
            return new ResponseEntity<String>(ms.getMessage("",null,req.getLocale()),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * redis 에서 email, hashKey 를 사용해 인증번호 값을 가져옴
     * 두 값을 비교해서 일치하면 email 인증 성공 데이터를 redis 에 저장하고 HttpStatus.OK 를 보냄 (submit 버튼 활성화)
     * 실패했다면 인증번호를 초기화하고 사용자에게 인증 실패를 알림
     * (javascript 코드를 통해서 실패했을 시에는 버튼이 다시 인증 전송 버튼으로 바뀜)
     * 
     * update: html 단에서 type 을 추가하여 어떤 유형인지 구분할 수 있도록 하여 상수 데이터에 어떤 타입인지 알려 redis 에 저장할 수 있도록 함
     * @param data : 파싱 데이터
     * @return 분기에 따른 데이터 전송
     */
    @PostMapping("/email/check")
    public ResponseEntity<String> checkVerification(@RequestBody HashMap<String, String> data, HttpServletRequest req) {
        String code = data.get("code");
        String email = data.get("email");
        String type = data.get("type");

        String validateCode = redisService.getData(email, RedisConst.EMAIL.get(type));
        if (code.equals(validateCode)) {
            log.info("success valid");
            redisService.setStringData(email, RedisConst.AUTH.get(type), StatusCode.OK.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            log.info("fail valid");
            redisService.deleteDataByString(email, RedisConst.EMAIL.get());
            return new ResponseEntity<>(ms.getMessage("fail.validation",null,req.getLocale()),HttpStatus.NOT_FOUND);
        }
    }
}
