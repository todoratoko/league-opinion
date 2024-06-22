package com.example.demo.services;

import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CornJobs {
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void checkInactiveUsers() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<User> inactiveUsers = userRepository.findUserByLastLoginBefore(thirtyDaysAgo);
        for (User user: inactiveUsers) {
            emailService.sendEmailNotLoggedForMonth(user.getEmail());
        }
    }

    //ToDo
        @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
        public static void checkIfMatchHasFinished () {
            //  select matches where matchStartDateTime < now();
            // set match to finishedMatch = true; ??
        }




    }

