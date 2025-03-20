package com.team14.clientProject.loginPage;

import com.team14.clientProject.sessionExpired.SessionValidationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public SessionValidationInterceptor sessionValidationInterceptor() {
        return new SessionValidationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionValidationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/reset-password", "/session-expired", "/logout");
    }
}
