package com.team14.clientProject.loginPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;



@Controller
@RequestMapping("/login")
@EnableWebSecurity
public class LoginPageController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping()
    public String loginPage() {
        return "hello";
    }
}
