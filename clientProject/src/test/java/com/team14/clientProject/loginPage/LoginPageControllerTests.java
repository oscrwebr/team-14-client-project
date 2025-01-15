package com.team14.clientProject.loginPage;

import com.team14.clientProject.homePage.HomePageRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.test.web.servlet.result.FlashAttributeResultMatchers;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class LoginPageControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private HomePageRepositoryImpl homePageRepositoryImpl;


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void homePageShows() throws Exception {
        MvcResult result = mvc
                .perform(get("/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void homePageShowsForUser() throws Exception {
        MvcResult result = mvc
                .perform(get("/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminPageShowsForAdmin() throws Exception {
        MvcResult result = mvc
                .perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void adminPageDoesNotShowForUser() throws Exception {
        MvcResult result = mvc
                .perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }




}
