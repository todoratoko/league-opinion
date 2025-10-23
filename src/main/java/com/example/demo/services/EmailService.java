package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Getter
@Setter
public class EmailService {
    @Value("${mail.smtp.auth}")
    private String mailAuth;
    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.starttls.enable}")
    private String ttlEnabled;
    @Value("${mail.senderEmail}")
    private String senderEmail;
    @Value("${mail.senderPassword}")
    private String senderPassword;
    @Value("${mail.smtp.port}")
    private String port;
    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailService() {}

    // Constructor for testing
    public EmailService(String mailAuth, String host, String ttlEnabled,
                        String senderEmail, String senderPassword, String port) {
        this.mailAuth = mailAuth;
        this.host = host;
        this.ttlEnabled = ttlEnabled;
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.port = port;
    }

    @Async
    public void sendEmailConfirmation(String recipient, String subject, String msg, String token) {
        Message message = prepareMessage(recipient, subject, msg + " click here to confirm - http://localhost:8081/reg/confirm?token=" + token);
        try {
                Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email send failed - " + e.getMessage());
        }
    }

    public void sendEmailForgotPassword(User foundUser, String token) {
        Message message = prepareMessage(foundUser.getEmail(), "Reset your password", "In order to reset your password click here - " + frontendUrl + "/reset-password?token=" + token);
        try {
                Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email send failed - " + e.getMessage());
        }
    }

    public void sendEmailNotLoggedForMonth(String email) {
        Message message = prepareMessage(email, "Come back!", "It looks like you haven't logged in for a while. We miss you!. Come back!");
        try {
                Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email send failed - " + e.getMessage());
        }
    }

    protected Message prepareMessage(String recipient, String subject, String msg) {
        if (mailAuth == null || ttlEnabled == null || host == null || port == null || senderEmail == null || senderPassword == null) {
            throw new NotFoundException("One or more email configuration parameters are missing");
        }
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", mailAuth);
        properties.put("mail.smtp.starttls.enable", ttlEnabled);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        String myAccount = senderEmail;
        String password = senderPassword;
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccount, password);
            }
        });
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(myAccount));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(msg);
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }


}
