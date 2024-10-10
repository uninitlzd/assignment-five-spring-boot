package com.alfredoeka.spring_assignment_five.service;

import com.alfredoeka.spring_assignment_five.entity.oauth.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private TemplateEngine templateEngine;


    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Registration");
        message.setText("");
        mailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }

    public void sendAfterRegistrationEmail(User user, String otp) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullname", user.getFullname());
            variables.put("otp", otp);

            sendHtmlMessage(
                    user.getUsername(),
                    "Assignment Five OTP Confirmation",
                    "registration-email-template",
                    variables
            );
        } catch (MessagingException e) {
             log.error("Failed to send email", e);
        }
    }

    public void sendForgotPasswordEmail(User user, String otp) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullname", user.getFullname());
            variables.put("otp", otp);

            sendHtmlMessage(
                    user.getUsername(),
                    "Forgot Password - Assignment Five",
                    "forgot-password-email-template",
                    variables
            );
        } catch (MessagingException e) {
            // Handle email sending failure
             log.error("Failed to send email", e);
        }
    }
}
