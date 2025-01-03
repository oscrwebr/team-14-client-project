package com.team14.clientProject.passwordReset;

import com.team14.clientProject.adminPage.AdminService;
import com.team14.clientProject.adminPage.User;
import com.team14.clientProject.emailPage.mail.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testConfirmResetPassword_InvalidToken() throws Exception {
        String invalidToken = "expiredOrInvalidToken";

        Mockito.when(adminService.isResetTokenValid(invalidToken)).thenReturn(false);

        mockMvc.perform(get("/reset-passw" +
                        "ord/confirm").param("token", invalidToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("error", "Invalid or expired token."));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testResetPassword_MismatchedPasswords() throws Exception {
        String token = "validToken";

        mockMvc.perform(post("/reset-password/confirm")
                        .param("token", token)
                        .param("newPassword", "password123")
                        .param("confirmPassword", "password321")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "New password and confirmation password do not match."));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testResetPassword_Valid() throws Exception {
        String token = "validToken";
        String email = "testuser@example.com";
        String newPassword = "newPassword123";

        Mockito.when(adminService.getEmailByResetToken(token)).thenReturn(email);
        Mockito.when(adminService.getUserByEmail(email)).thenReturn(new User(1, "testuser", "John", "Doe", email, "ROLE_USER", null, null));

        mockMvc.perform(post("/reset-password/confirm")
                        .param("token", token)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "Password reset successful!"));
    }
}
