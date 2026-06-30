package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            Context context = new Context();
            // TODO: Move this URL to application properties
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;
            context.setVariable("resetUrl", resetUrl);

            String htmlBody = templateEngine.process("reset-password-email", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom("noreply@restapi.com");
            helper.setTo(toEmail);
            helper.setSubject("Yêu cầu khôi phục mật khẩu - RestAPI Identity");
            helper.setText(htmlBody, true);

            javaMailSender.send(mimeMessage);
            log.info("Reset password email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}", toEmail, e);
        }
    }
}
