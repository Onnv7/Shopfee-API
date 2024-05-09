package com.hcmute.shopfee.service.common;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "verify_email_template";
    public static final String CODE_TEMPLATE = "verify_code_template";
    public static final String USER_BLOCKED_TEMPLATE = "user_blocked_bc_boom_order";
    public static final String TEXT_HTML_ENCONDING = "text/html";
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;


    @Async
    public void sendHtmlVerifyCodeToRegister(String to, String code) {
        try {
            Context context = new Context();
            context.setVariables(Map.of( "code", code));
            String text = templateEngine.process(CODE_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            emailSender.send(message);

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Async
    public void sendUserBlocked(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariables(Map.of( "user_name", userName));
            String text = templateEngine.process(USER_BLOCKED_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("LOCK SHOPFEE ACCOUNT");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            emailSender.send(message);

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }


    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }
}
