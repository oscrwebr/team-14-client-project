package com.team14.clientProject.loggingSystem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//overrides the preHandle and afterCompletion methods to log requests and responses
@Component
public class CustomLogInterceptor implements HandlerInterceptor {

    @Autowired
    private SystemLogRepositoryImpl systemLogRepository;
    //overrides HandlerInterceptor's preHandle method to log requests before they are handled
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        try {
            //will fail if no user is logged in
            String requestUser = request.getUserPrincipal().getName();
        } catch (Exception e) {
            String requestUser = "server";}
        // extra logging for requests not necessary
        String requestType = "API";
        String requestAction = "Request";
        String requestNotes = "Request made to " + requestURI + " using " + requestMethod + " method";

        // calls logging functionality to add it to database
        systemLogRepository.addTraversalLog(requestNotes);

        return true;
    }
    //overrides HandlerInterceptor's afterCompletion method to log responses after they are handled
    // code same as above
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String requestUser = "server";
        try {
            requestUser = request.getUserPrincipal().getName();
        } catch (Exception e) {
            throw e;
        }
        String requestType = "API";
        String requestAction = "Response";
        String requestNotes = "Response sent to " + requestUser + " for request made to " + requestURI + " using " + requestMethod + " method";

        systemLogRepository.addTraversalLog(requestNotes);
    }
}
