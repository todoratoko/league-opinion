package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
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

    @Async
    public void sendEmailConfirmation(String recipient, String subject, String msg, String token) {
        Message message = prepareMessage(recipient, subject, msg + " click here to confirm - http://localhost:8080/reg/confirm?token=" + token);
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email send failed - " + e.getMessage());
        }
    }

    private Message prepareMessage(String recipient, String subject, String msg) {
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
