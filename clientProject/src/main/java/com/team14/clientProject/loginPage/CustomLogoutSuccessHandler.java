package com.team14.clientProject.loginPage;

import com.team14.clientProject.adminPage.AdminRepositoryImpl;
import com.team14.clientProject.adminPage.User;
import com.team14.clientProject.loggingSystem.SystemLogRepositoryImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private SystemLogRepositoryImpl systemLogRepository;

    @Autowired
    private AdminRepositoryImpl adminRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            String username = authentication.getName();
            User user = adminRepository.findByUsername(username);
            systemLogRepository.logoutUser(user.getId());
        }

        
        response.sendRedirect("/login?logout");
    }
}
