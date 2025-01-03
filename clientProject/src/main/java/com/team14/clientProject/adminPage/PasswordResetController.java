package com.team14.clientProject.adminPage;

import com.team14.clientProject.emailPage.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@RestController
@RequestMapping("/reset-password")
public class PasswordResetController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${reset.password.url:http://localhost:8080/reset-password/confirm?token=}")
    private String resetPasswordBaseUrl;

    @GetMapping
    public ModelAndView resetPasswordPage() {
        return new ModelAndView("passwordReset/newPassword");
    }

    @PostMapping
    public ModelAndView resetPassword(
            @RequestParam String email,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        User user = adminService.getUserByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid email address.");
            return new ModelAndView("redirect:/reset-password");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return new ModelAndView("redirect:/reset-password");
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation password do not match.");
            return new ModelAndView("redirect:/reset-password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        adminService.updateUser(user);

        redirectAttributes.addFlashAttribute("success", "Password reset successful!");
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/send-reset-link")
    public ModelAndView sendResetLink(@RequestParam String email, RedirectAttributes redirectAttributes) {
        User user = adminService.getUserByEmail(email);
        ModelAndView modelAndView = new ModelAndView("admin/admin");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User with the given email does not exist.");
            modelAndView.addObject("error", "User with the given email does not exist.");
            return new ModelAndView("redirect:/admin");
        }

        adminService.invalidateResetTokensByEmail(email);

        String resetToken = UUID.randomUUID().toString();
        adminService.saveResetToken(email, resetToken);

        String resetLink = "http://localhost:8080/reset-password/confirm?token=" + resetToken;

        String emailContent = "<html>" +
                "<body>" +
                "<p>Dear " + user.getFirstName() + ",</p>" +
                "<p>You requested a password reset. Click the link below to reset your password:</p>" +
                "<a href='" + resetLink + "'>Reset Password</a>" +
                "<br><br>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Thank you,<br>Admin Team</p>" +
                "</body>" +
                "</html>";

        try {
            emailService.sendHtmlMessageWithLogo(
                    user.getEmail(),
                    "Password Reset Request",
                    emailContent,
                    "src/main/resources/static/images/dhcw.png"
            );
            modelAndView.addObject("success", "Password reset link sent to " + email + ".");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Failed to send password reset link.");
        }

        return new ModelAndView("passwordResetForm");
    }


    @GetMapping("/confirm")
    public ModelAndView confirmResetPage(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        if (!adminService.isResetTokenValid(token)) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("passwordResetForm");
    }


    @PostMapping("/confirm")
    public ModelAndView resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation password do not match.");
            return new ModelAndView("redirect:/reset-password/confirm?token=" + token);
        }

        String email = adminService.getEmailByResetToken(token);
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return new ModelAndView("redirect:/login");
        }

        User user = adminService.getUserByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return new ModelAndView("redirect:/login");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        adminService.updateUser(user);

        adminService.invalidateResetToken(token);

        redirectAttributes.addFlashAttribute("success", "Password reset successful!");
        return new ModelAndView("redirect:/login");
    }
}
