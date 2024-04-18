package com.innerspaces.innerspace.services;

import com.innerspaces.innerspace.models.EmailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

@Autowired
    public EmailServiceImpl(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendRegistrationEmail(EmailModel model, Context context) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(model.getTo());
            helper.setSubject(model.getSubject());
            String htmlContent = templateEngine.process(model.getTemplateName(), context);
            helper.setText(htmlContent, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle exception
        }
    }

    public void sendForgotPasswordEmail(EmailModel model, String otp, Context context) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(model.getTo());
            helper.setSubject(model.getSubject());
            String htmlContent = templateEngine.process(model.getTemplateName(), context);
            helper.setText(htmlContent, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw e;
        }
    }

}
