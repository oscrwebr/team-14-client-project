package com.team14.clientProject.emailPage.mail;

import com.team14.clientProject.profilePage.Profile;
import com.team14.clientProject.profilePage.ProfilePageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConcurrentEmailServiceHandler {
    @Autowired
    private EmailAsyncSender emailAsyncSender;


    public String sendEmails(List<String> emailIds, String subject, String htmlBody, String logoPath) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (String emailId : emailIds) {
            int profileId = Integer.parseInt(emailId);
            futures.add(emailAsyncSender.sendEmailAsync(profileId, subject, htmlBody, logoPath));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        StringBuilder alertMessages = new StringBuilder();
        for (CompletableFuture<String> future : futures) {
            String result = future.join();
            if (!result.isEmpty()) alertMessages.append(result);
        }

        return alertMessages.length() > 0 ? alertMessages.toString() : "Emails sent successfully (async).";
    }

}
