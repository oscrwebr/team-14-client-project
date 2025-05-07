package com.team14.clientProject.emailPage.mail;

import com.team14.clientProject.profilePage.Profile;
import com.team14.clientProject.profilePage.ProfilePageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailAsyncSender {
    @Autowired
    private ProfilePageRepository profilePageRepository;

    @Autowired
    private EmailService emailService;

    @Async("taskExecutor")
    public CompletableFuture<String> sendEmailAsync(int profileId, String subject, String htmlBody, String logoPath) {
        System.out.println("Running on: " + Thread.currentThread().getName());
        Profile profile = profilePageRepository.getProfileById(profileId);
        if (profile == null) return CompletableFuture.completedFuture("");

        String email = profile.getEmail();
        if (email == null || email.isEmpty()) {
            return CompletableFuture.completedFuture("Email is empty for user ID " + profileId);
        }

        String regexPattern = "^[a-zA-Z0-9_!#$%&*+/=?`{}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!EmailValidation.patternMatches(email, regexPattern)) {
            return CompletableFuture.completedFuture("Invalid email format for user ID " + profileId);
        }

        try {
            emailService.sendHtmlMessageWithLogo(email, subject, htmlBody, logoPath);
            return CompletableFuture.completedFuture("");
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Failed to send email to user ID " + profileId);
        }
    }
}
