package com.team14.clientProject.userProfile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // Code adapted from https://www.baeldung.com/get-user-in-spring-security
    @GetMapping("/user-profile")
    public ModelAndView displayPage(Principal principal) {
        String currentUser = principal.getName();
        ModelAndView modelAndView = new ModelAndView("userProfile/userProfilePage");

        User user = userProfileService.getUserProfile(currentUser);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
