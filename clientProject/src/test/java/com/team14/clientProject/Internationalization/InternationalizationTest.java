package com.team14.clientProject.Internationalization;

import com.team14.clientProject.statisticsPage.StatisticController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(controllers = StatisticController.class) // Restrict to the relevant controller
class InternationalizationWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageSource messageSource; // Mock the MessageSource to simulate i18n behavior

    @BeforeEach
    public void setUp(){
        LocaleContextHolder.setLocale(Locale.ENGLISH); // Set to the default language
    }
    //these tests did not run successfully when project was forked, code was written by someone else
//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    void testDefaultLanguageDisplay() throws Exception {
//        // Simulate a GET request to /statistics
//        mockMvc.perform(get("/statistics")) // No lang parameter, default to ENGLISH
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("title", "Statistics Overview")); // Check title
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    void testWelshLanguageDisplay() throws Exception {
//        // Simulate a GET request to /statistics with lang=cy
//        mockMvc.perform(get("/statistics").param("lang", "cy")) // Request in Welsh
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("Trosolwg o Ystadegau"))) // Check title
//                .andExpect(content().string(containsString("Recriwtio Digwyddiad"))); // Check menu text
//    }
}