package com.team14.clientProject.loginPage;

import com.team14.clientProject.adminPage.AdminRepositoryImpl;
import com.team14.clientProject.loggingSystem.SystemLogRepositoryImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import com.team14.clientProject.adminPage.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AdminRepositoryImpl AdminRepository;

    @Autowired
    private SystemLogRepositoryImpl SystemLogRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = AdminRepository.findByUsername(username);
        if (user != null) {
            SystemLogRepository.loginUser(user.getId());
        }

        // Redirect to default success URL
        response.sendRedirect("/home");
    }
}
