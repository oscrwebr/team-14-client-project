package com.team14.clientProject.emailPage.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendHtmlMessageWithLogo(String to, String subject, String htmlBody, String logoPath) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sbo753130@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        File logo = new File(logoPath);
        helper.addInline("logo", logo);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlMessageWithAttachment(String to, String subject, String htmlBody, byte[] pdfAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sbo753130@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addAttachment("attachment.pdf", () -> new ByteArrayInputStream(pdfAttachment));
        emailSender.send(message);
    }


    @Override
    public void sendWarningEmail(String to, String subject, String htmlBody, String logoPath) throws MessagingException {
        String htmlContent = htmlBody + "<br><img src='cid:logo'>";
        sendHtmlMessageWithLogo(to, subject, htmlContent, logoPath);
        System.out.println("Warning email sent to: " + to);
    }
}

