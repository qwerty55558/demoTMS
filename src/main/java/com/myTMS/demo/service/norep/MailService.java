package com.myTMS.demo.service.norep;

import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dto.MailTemplate;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.utils.RandomAuthCodeGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final MessageSource ms;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    String sender;

    @Async
    public void send(MailTemplate template){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 발신자 설정
            helper.setFrom(sender);

            // 메일 제목 설정
            helper.setSubject(template.getTitle());

            // 수신자 설정
            helper.setTo(template.getTo());

            // 템플릿에 전달될 데이터 설정
            Context context = new Context();
            template.getValues().forEach(context::setVariable);

            // 메일 내용 설정
            String html = templateEngine.process("email/" + template.getTemplateName(), context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.info("MailSendError", e);
        }
    }

    public MailTemplate buildAuthTemplate(String email, Locale locale, String type) {
        Map<String, String> map = new HashMap<>();

        String[] authCode = new String[1];
        authCode[0] = RandomAuthCodeGenerator.generate();

        map.put("title", ms.getMessage("email.valid.welcome", null, locale));
        map.put("message", ms.getMessage("email.valid.code", authCode, locale));
        map.put("link", ms.getMessage("email.valid.link", null, locale));

        MailTemplate mail = new MailTemplate.MailBuilder()
                .to(email)
                .title(ms.getMessage("email.valid.title", null, locale))
                .values(map)
                .templateName("authTemplate")
                .build();

        if (type != null) {
            redisService.setStringData(email, RedisConst.EMAIL.get(type),authCode[0]);
            log.info("code = {}", authCode[0]);
        }

        return mail;
    }

    public MailTemplate buildPwResetTemplate(String email, Locale locale, String tempPw) {
        Map<String, String> map = new HashMap<>();

        String[] tempPwArr = new String[1];
        tempPwArr[0] = tempPw;

        map.put("title", ms.getMessage("email.resetpw.welcome", null, locale));
        map.put("message", ms.getMessage("email.resetpw.code", tempPwArr, locale));
        map.put("link", ms.getMessage("email.resetpw.link", null, locale));

        log.info("tempPw = {}", tempPw);

        return new MailTemplate.MailBuilder()
                .to(email)
                .title(ms.getMessage("email.resetpw.title", null, locale))
                .values(map)
                .templateName("authTemplate")
                .build();
    }

    public MailTemplate buildEmpTempPwTemplate(String email, Locale locale, String tempPw) {
        Map<String, String> map = new HashMap<>();

        String[] tempPwArr = new String[1];
        tempPwArr[0] = tempPw;

        map.put("title", ms.getMessage("email.employee.welcome", null, locale));
        map.put("message", ms.getMessage("email.employee.code", tempPwArr, locale));
        map.put("link", ms.getMessage("email.employee.link", null, locale));

        log.info("tempPw = {}", tempPw);

        return new MailTemplate.MailBuilder()
                .to(email)
                .title(ms.getMessage("email.employee.title", null, locale))
                .values(map)
                .templateName("authTemplate")
                .build();

    }
}
