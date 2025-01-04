package com.team14.clientProject.sessionExpired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession(false) == null) {
            response.sendRedirect("/session-expired");
            return false;
        }
        Object user = request.getSession().getAttribute("userLoggedIn");
        if (user == null) {
            response.sendRedirect("/session-expired");
            return false;
        }
        return true;
    }
}
