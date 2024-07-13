package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class EmailServiceTests {
    @InjectMocks
    private EmailService emailService;

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

    @BeforeEach
    void setUp(){
        emailService.setMailAuth(mailAuth);
        emailService.setHost(host);
        emailService.setTtlEnabled(ttlEnabled);
        emailService.setSenderEmail(senderEmail);
        emailService.setSenderPassword(senderPassword);
        emailService.setPort(port);
    }

    @Test
    void shouldSendEmailConfirmation() {
        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            emailService.sendEmailConfirmation("recipient@example.com", "Subject", "Message", "token");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }
    @Test
    void shouldThrowExceptionWhenMessageIsNotPreparedForConfirmation() {
        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class))).thenThrow(new MessagingException("Mock Exception"));

            emailService.sendEmailConfirmation("recipient@example.com", "Subject", "Message", "token");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void shouldSendEmailForgotPassword() {
        User user = new User();
        user.setEmail("user@example.com");
        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            emailService.sendEmailForgotPassword(user, "token");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void shouldThrowExceptionWhenMessageIsNotPreparedForForgotPassword() throws MessagingException {
        User user = new User();
        user.setEmail("user@example.com");

        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new MessagingException("Test exception"));

            emailService.sendEmailForgotPassword(user, "token");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void shouldSendEmailNotLoggedForMonth() {
        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            emailService.sendEmailNotLoggedForMonth("user@example.com");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }
    @Test
    void shouldThrowExceptionWhenMessageWasNotPreparedForNotLoggedForMonth() throws MessagingException {
        try (MockedStatic<Transport> transportMockedStatic = Mockito.mockStatic(Transport.class)) {
            transportMockedStatic.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new MessagingException("Test exception"));

            emailService.sendEmailNotLoggedForMonth("user@example.com");

            transportMockedStatic.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void shouldPrepareMessage() {
        String recipient = "recipient@example.com";
        String subject = "Subject";
        String msg = "Message";

        Message message = emailService.prepareMessage(recipient, subject, msg);

        assertNotNull(message);
        try {
            assertEquals(senderEmail, ((InternetAddress) message.getFrom()[0]).getAddress());
            assertEquals(recipient, ((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
            assertEquals(subject, message.getSubject());
            assertEquals(msg, message.getContent());
        } catch (Exception e) {
            fail("Exception occurred while testing prepareMessage: " + e.getMessage());
        }
    }
    @Test
    void shouldThrowNotFoundExceptionWhenAnyConfigurationIsNullWhenPreparingMessage() {
        String[] params = {"mailAuth", "ttlEnabled", "host", "port", "senderEmail", "senderPassword"};
        for (String param : params) {
            EmailService testService = new EmailService(
                    param.equals("mailAuth") ? null : "value",
                    param.equals("host") ? null : "value",
                    param.equals("ttlEnabled") ? null : "value",
                    param.equals("senderEmail") ? null : "value",
                    param.equals("senderPassword") ? null : "value",
                    param.equals("port") ? null : "value"
            );

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                testService.prepareMessage("recipient@example.com", "Subject", "Message");
            });

            assertEquals("One or more email configuration parameters are missing", exception.getMessage());
        }
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {
        String recipient = "recipient@example.com";
        String subject = "Special & Characters";
        String msg = "Message with special characters: !@#$%^&*()";

        Message message = emailService.prepareMessage(recipient, subject, msg);

        assertNotNull(message);
        try {
            assertEquals(senderEmail, ((InternetAddress) message.getFrom()[0]).getAddress());
            assertEquals(recipient, ((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
            assertEquals(subject, message.getSubject());
            assertEquals(msg, message.getContent());
        } catch (Exception e) {
            fail("Exception occurred while testing prepareMessage with special characters: " + e.getMessage());
        }
    }

    @Test
    void shouldHandleLargeEmailContent() {
        String recipient = "recipient@example.com";
        String subject = "Subject";
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            msg.append("Message content ");
        }

        Message message = emailService.prepareMessage(recipient, subject, msg.toString());

        assertNotNull(message);
        try {
            assertEquals(senderEmail, ((InternetAddress) message.getFrom()[0]).getAddress());
            assertEquals(recipient, ((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
            assertEquals(subject, message.getSubject());
            assertEquals(msg.toString(), message.getContent());
        } catch (Exception e) {
            fail("Exception occurred while testing prepareMessage with large content: " + e.getMessage());
        }
    }


}





