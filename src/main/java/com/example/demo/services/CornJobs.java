package com.example.demo.services;

import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import com.example.demo.services.pandascore.PandaScoreSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CornJobs {
    private static final Logger logger = LoggerFactory.getLogger(CornJobs.class);

    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PandaScoreSyncService pandaScoreSyncService;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void checkInactiveUsers() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<User> inactiveUsers = userRepository.findUserByLastLoginBefore(thirtyDaysAgo);
        for (User user: inactiveUsers) {
            emailService.sendEmailNotLoggedForMonth(user.getEmail());
        }
    }

    /**
     * Sync upcoming matches from PandaScore
     * Runs every 6 hours
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 6)  // Every 6 hours
    public void syncUpcomingMatches() {
        logger.info("Starting scheduled sync of upcoming matches from PandaScore");
        try {
            int synced = pandaScoreSyncService.syncUpcomingMatches(14);  // Sync next 14 days
            logger.info("Scheduled sync completed: {} matches synced", synced);
        } catch (Exception e) {
            logger.error("Error during scheduled match sync", e);
        }
    }

    //ToDo

//        @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
//        public static void checkIfMatchHasFinished () {
//            //  select matches where matchStartDateTime < now();
//            // set match to finishedMatch = true; ??
//        }




    }

