package com.team14.clientProject.emailPage.mail;

import com.team14.clientProject.emailPage.mail.EmailService;
import com.team14.clientProject.emailPage.mail.EmailValidation;
import com.team14.clientProject.profilePage.Profile;
import com.team14.clientProject.profilePage.ProfilePageRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

// Service layer to handle the email sending logic and validation
@Service
public class EmailServiceHandler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfilePageRepository profilePageRepository;

    public String sendEmails(List<String> emailIds, String subject, String htmlBody, String logoPath) {
        // https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
        // StringBuilder is used to collect error messages for invalid emails;
        // And concatenate them into a single string
        StringBuilder alertMessages = new StringBuilder();

        // Iterate through each email ID to process and send emails
        for (String emailId : emailIds) {
            int profileId = Integer.parseInt(emailId);
            Profile profile = profilePageRepository.getProfileById(profileId);
            if (profile != null) {
                String emailAddress = profile.getEmail();
                // Check if the email address is empty
                if (emailAddress == null || emailAddress.isEmpty()) {
                    alertMessages.append("Email address is empty for user ID ").append(profileId).append(". ");
                    continue;
                }

                // Validate the email address format - RFC 5322 Format
                String regexPattern = "^[a-zA-Z0-9_!#$%&*+/=?`{}~^.-]+@[a-zA-Z0-9.-]+$";
                if (!EmailValidation.patternMatches(emailAddress, regexPattern)) {
                    alertMessages.append("Invalid email format for user ID ").append(profileId).append(". ");
                    continue;
                }

                // Attempt to send the email.
                try {
                    emailService.sendHtmlMessageWithLogo(emailAddress, subject, htmlBody, logoPath);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    alertMessages.append("Failed to send email to user ID ").append(profileId).append(". ");
                }
            }
        }

        // Return the collected alert messages or a success message
        return alertMessages.length() > 0 ? alertMessages.toString() : "Emails sent successfully to selected addresses.";
    }

    public void sendNewsletterEmail(String to, String subject, String htmlBody, byte[] pdfAttachment)
            throws MessagingException {
        emailService.sendHtmlMessageWithAttachment(to, subject, htmlBody, pdfAttachment);
    }

    public void sendBulkNewsletterEmails(List<String> emailAddresses, String subject, String htmlBody, byte[] pdfAttachment)
            throws MessagingException {
        for (String email : emailAddresses) {
            sendNewsletterEmail(email, subject, htmlBody, pdfAttachment);
        }
    }


}