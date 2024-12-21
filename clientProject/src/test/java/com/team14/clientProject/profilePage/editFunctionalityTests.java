package com.team14.clientProject.profilePage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class editFunctionalityTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProfilePageRepositoryImpl profilePageRepository;
    private MockMvc Mvc;

    @Test
    public void testEditButtonDisplayedOnIndividualProfilePages() throws Exception {
        Mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = Mvc.perform(get("/profile/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("<button id=\"editButton\""));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testEditFirstName() throws Exception {
        Mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Mvc.perform(post("/profile/edit/1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("location", "New York")
                        .param("email", "john.doe@example.com")
                        .param("phoneNumber", "1234567890")
                        .param("currentPosition", "Developer")
                        .param("status", "Internal")
                        .param("skill", "Java")
                        .param("eventAttended", "Spring Boot Conference"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Profile updatedProfile = profilePageRepository.getProfileById(1);

        assertTrue(updatedProfile.getFirstName().equals("John"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testEditSubscribeToBulletins() throws Exception {
        Mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Mvc.perform(post("/profile/edit/1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("location", "New York")
                        .param("email", "john.doe@example.com")
                        .param("phoneNumber", "1234567890")
                        .param("currentPosition", "Developer")
                        .param("status", "Internal")
                        .param("skill", "Java")
                        .param("eventAttended", "Spring Boot Conference")
                        .param("subscribeToBulletins", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Profile updatedProfile = profilePageRepository.getProfileById(1);

        assertTrue(updatedProfile.getPreferences().isSubscribeToBulletins());
    }
}


