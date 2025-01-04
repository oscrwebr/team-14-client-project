package com.team14.clientProject.sessionExpired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionExpiredController {


    @GetMapping("/session-expired")
    public String sessionExpired() {
        return "session-expired";
    }
}
