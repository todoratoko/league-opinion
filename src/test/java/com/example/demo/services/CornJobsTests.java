package com.example.demo.services;

import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CornJobsTests {
    @InjectMocks
    CornJobs cornJobs;
    @Mock
    EmailService emailService;
    @Mock
    UserRepository userRepository;
    private List<User> inactiveUsers;

    @BeforeEach
    public void setUp(){
        inactiveUsers = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        user1.setEmail("user1@example.com");
        user2.setEmail("user2@example.com");
        inactiveUsers.add(user1);
        inactiveUsers.add(user2);
    }

    @Test
    public void shouldSendEmailToInactiveUsers(){
        LocalDate thirtyDatsAgo = LocalDate.now().minusDays(30);

        when(userRepository.findUserByLastLoginBefore(thirtyDatsAgo)).thenReturn(inactiveUsers);

        cornJobs.checkInactiveUsers();

        verify(userRepository,times(1)).findUserByLastLoginBefore(thirtyDatsAgo);
        verify(emailService, times(1)).sendEmailNotLoggedForMonth("user1@example.com");
        verify(emailService, times(1)).sendEmailNotLoggedForMonth("user2@example.com");
    }

    @Test
    public void shouldNotSendEmailToInactiveUsers(){
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        when(userRepository.findUserByLastLoginBefore(thirtyDaysAgo)).thenReturn(new ArrayList<>());

        cornJobs.checkInactiveUsers();

        verify(userRepository, times(1)).findUserByLastLoginBefore(thirtyDaysAgo);
        verify(emailService, times(0)).sendEmailNotLoggedForMonth(anyString());
    }


}
