package com.team14.clientProject.automaticDeletion;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import com.team14.clientProject.emailPage.mail.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AutomaticDeletion {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private boolean emailSent = false;

    @Scheduled(cron = "*/15 * * * * ?") // every 15 seconds
    public void checkApplicants() throws MessagingException, InterruptedException {
        if (emailSent) {
            return;
        }

        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);

        System.out.println("Checking applicants created before " + thirtySecondsAgo);

        String warnSql = "SELECT email FROM applicants WHERE createdAt < ? LIMIT 1";
        List<String> emailsToWarn = jdbcTemplate.queryForList(warnSql, String.class, thirtySecondsAgo);

        if (!emailsToWarn.isEmpty()) {
            System.out.println("Sending warning email to: " + emailsToWarn.get(0));
            String htmlBody = "<p>Your information will be automatically deleted in 1 month in accordance with company policy. Please contact us to retain your information.</p>";
            emailService.sendWarningEmail(emailsToWarn.get(0), "Warning: Your information will be deleted soon", htmlBody, "");
            emailSent = true;
            TimeUnit.SECONDS.sleep(15);

            String moveSql = "INSERT INTO deletedApplicants (id, firstName, lastName, location, email, phoneNumber, currentPosition, status, skill, eventAttended, SubscribeToNewsLetter, SubscribeToBulletins, SubscribeToJobUpdates) " +
                    "SELECT a.id, a.firstName, a.lastName, a.location, a.email, a.phoneNumber, " +
                    "d.currentPosition, d.status, a.skill, a.eventAttended, " +
                    "p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates " +
                    "FROM applicants a " +
                    "LEFT JOIN applicantPreferences p ON a.Id = p.applicationId " +
                    "LEFT JOIN applicationDetails d ON a.Id = d.applicationId " +
                    "WHERE a.createdAt < ? LIMIT 1";
            int rowsMoved = jdbcTemplate.update(moveSql, thirtySecondsAgo);
            System.out.println("Rows moved to deletedApplicants: " + rowsMoved);

            String deleteSql = "DELETE FROM applicants WHERE createdAt < ? LIMIT 1";
            int rowsDeleted = jdbcTemplate.update(deleteSql, thirtySecondsAgo);
            System.out.println("Rows deleted from applicants: " + rowsDeleted);

            // Publish an event after deletion
            eventPublisher.publishEvent(new ProfileListUpdatedEvent(this));
        }
    }
}