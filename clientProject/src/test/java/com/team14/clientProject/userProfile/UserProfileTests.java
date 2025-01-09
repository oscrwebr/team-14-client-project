package com.team14.clientProject.userProfile;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    public void testMePageDisplaysCorrectUser() throws Exception{
        User mockUser = new User("testUser", "John", "Doe", "ROLE_USER");

        when(userProfileService.getUserProfile("testUser")).thenReturn(mockUser);


        MvcResult result = mvc.perform(get("/user-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("userProfile/userProfilePage"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        // Username is unique so false positive is impossible
        assertTrue(content.contains("testUser"));

    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    public void testMePageDisplaysCorrectRole() throws Exception{
        User mockUser = new User("testUser", "John", "Doe", "ROLE_USERTEST");

        when(userProfileService.getUserProfile("testUser")).thenReturn(mockUser);


        MvcResult result = mvc.perform(get("/user-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("userProfile/userProfilePage"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("ROLE_USERTEST"));

    }

    @Test
    public void testPageNotAccessibleWithoutLoggingInFirst() throws Exception {
        mvc.perform(get("/user-profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


}
