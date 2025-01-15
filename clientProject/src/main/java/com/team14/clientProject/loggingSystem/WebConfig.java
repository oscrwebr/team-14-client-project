package com.team14.clientProject.loggingSystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.team14.clientProject.loggingSystem.CustomLogInterceptor;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomLogInterceptor customLogInterceptor;

    public WebConfig(CustomLogInterceptor customLogInterceptor) {
        this.customLogInterceptor = customLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register your custom interceptor here
        registry.addInterceptor(customLogInterceptor)
                .addPathPatterns("/**");  // This will apply the interceptor to all paths
    }
}
