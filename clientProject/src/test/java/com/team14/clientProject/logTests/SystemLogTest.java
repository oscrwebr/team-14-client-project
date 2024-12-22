package com.team14.clientProject.logTests;

import com.team14.clientProject.loggingSystem.SystemLogRepositoryImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class SystemLogTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private SystemLogRepositoryImpl systemLogRepositoryImpl;

    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        systemLogRepositoryImpl.clearLogs();
    }

    public SystemLogTest() {
        systemLogRepositoryImpl = new SystemLogRepositoryImpl(jdbcTemplate);
    }

    @Test
    void getLogs(){
        assertNotNull(systemLogRepositoryImpl.getLogs());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addUserLog(){
        int originalSize = systemLogRepositoryImpl.getLogs().size();
        systemLogRepositoryImpl.addUserLog();
        assertEquals("addedUser", systemLogRepositoryImpl.getLogs().get(0).getActionTaken());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void removeUserLog(){
        int originalSize = systemLogRepositoryImpl.getLogs().size();
        systemLogRepositoryImpl.removeUserLog(1);
        originalSize++;
        assertEquals(originalSize, systemLogRepositoryImpl.getLogs().size());
        assertEquals("removedUser", systemLogRepositoryImpl.getLogs().get(0).getActionTaken());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void loginUserLogActionIsCorrect() {
        systemLogRepositoryImpl.loginUser(1);
        assertEquals("login", systemLogRepositoryImpl.getLogs().get(0).getActionTaken());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void logoutUserLogActionIsCorrect() {
        systemLogRepositoryImpl.logoutUser(1);
        assertEquals("logout", systemLogRepositoryImpl.getLogs().get(0).getActionTaken());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getWhenUserLoggedInReturnsCorrectResult() throws InterruptedException {
        systemLogRepositoryImpl.loginUser(1);
        Thread.sleep(2000);
        //note to self, sleep bad, look into polling mechanism?
        systemLogRepositoryImpl.logoutUser(1);
        Thread.sleep(2000);
        String actualTimeUserLoggedIn = systemLogRepositoryImpl.getLogs().get(1).getTimestamp();
        String TimeUserLoggedInAccordingToGetWhenUserLoggedInFunction = systemLogRepositoryImpl.getWhenUserLoggedIn(1, systemLogRepositoryImpl.getLogs().get(0).getTimestamp()).get(0).getTimestamp();
        assertEquals(actualTimeUserLoggedIn, TimeUserLoggedInAccordingToGetWhenUserLoggedInFunction);
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getSessionLogsReturnsCorrectResult() throws InterruptedException {
        systemLogRepositoryImpl.loginUser(1);
        Thread.sleep(2000);
        systemLogRepositoryImpl.removeUserLog(3);
        Thread.sleep(2000);
        systemLogRepositoryImpl.logoutUser(1);
        Thread.sleep(2000);

        String loginTime = systemLogRepositoryImpl.getLogs().get(2).getTimestamp();
        String logoutTime = systemLogRepositoryImpl.getLogs().get(0).getTimestamp();
        assertEquals(3, systemLogRepositoryImpl.getSessionLogs(1, loginTime, logoutTime).size());
        assertEquals("login", systemLogRepositoryImpl.getSessionLogs(1, loginTime, logoutTime).get(2).getActionTaken());
        assertEquals("removedUser", systemLogRepositoryImpl.getSessionLogs(1, loginTime, logoutTime).get(1).getActionTaken());
        assertEquals("logout", systemLogRepositoryImpl.getSessionLogs(1, loginTime, logoutTime).get(0).getActionTaken());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void clearLogs(){
        systemLogRepositoryImpl.addUserLog();
        assertNotEquals(0, systemLogRepositoryImpl.getLogs().size());
        systemLogRepositoryImpl.clearLogs();
        assertEquals(0, systemLogRepositoryImpl.getLogs().size());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getLogoutLogsWorks(){
        assertEquals(0, systemLogRepositoryImpl.getLogoutLogs().size());
        systemLogRepositoryImpl.logoutUser(1);
        systemLogRepositoryImpl.addUserLog();
        systemLogRepositoryImpl.loginUser(1);
        assertEquals(1, systemLogRepositoryImpl.getLogoutLogs().size());
        assertEquals("logout", systemLogRepositoryImpl.getLogoutLogs().get(0).getActionTaken());
    }



    /*
------------------------------------------------FRONTEND------------------------------------------------
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAllSystemLogsShowOnAdminPage() throws Exception {
        MvcResult result = mvc
                .perform(get("/admin"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logsShownOnPage = doc.select(".SystemLogs");
        assertEquals(systemLogRepositoryImpl.getLogs().size(), logsShownOnPage.size());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAllsessionLogsShowOnAdminPage() throws Exception {
        assertEquals(0, systemLogRepositoryImpl.getLogoutLogs().size());
        systemLogRepositoryImpl.loginUser(1);
        Thread.sleep(2000);
        systemLogRepositoryImpl.logoutUser(1);
        Thread.sleep(2000);
        MvcResult result = mvc
                .perform(get("/admin"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logsShownOnPage = doc.select(".sessionLogs");
        assertEquals(1, systemLogRepositoryImpl.getLogoutLogs().size(), "There should be 1 logout log");
        assertEquals(systemLogRepositoryImpl.getLogoutLogs().size(), logsShownOnPage.size(), "All session logs should be displayed on the admin page");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUserSessionPageDisplaysGivenvalidParameters() throws Exception {
        systemLogRepositoryImpl.loginUser(1);
        Thread.sleep(2000);
        systemLogRepositoryImpl.logoutUser(1);
        Thread.sleep(2000);
        String logoutTime = systemLogRepositoryImpl.getLogs().get(0).getTimestamp();
        String userId = Integer.toString(systemLogRepositoryImpl.getLogs().get(0).getUserId());
        MvcResult result = mvc
                .perform(get("/admin/session/user/{userId}/{logoutTime}", userId, logoutTime))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/userSession"))
                .andReturn();
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUserSessionPageRequestReturnsToAdminGivenInvalidParameters() throws Exception {
        String logoutTime = "HayleyWelch";
        MvcResult result = mvc
                .perform(get("/admin/session/user/{userId}/{logoutTime}", 1, logoutTime))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUserSessionPageDisplaysAllLogs() throws Exception {
        systemLogRepositoryImpl.loginUser(1);
        Thread.sleep(2000);
        systemLogRepositoryImpl.removeUserLog(3);
        Thread.sleep(2000);
        systemLogRepositoryImpl.logoutUser(1);
        Thread.sleep(2000);
        String logoutTime = systemLogRepositoryImpl.getLogs().get(0).getTimestamp();
        String userId = Integer.toString(systemLogRepositoryImpl.getLogs().get(0).getUserId());
        MvcResult result = mvc
                .perform(get("/admin/session/user/{userId}/{logoutTime}", userId, logoutTime))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/userSession"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logsShownOnPage = doc.select(".logs");
        assertEquals(3, logsShownOnPage.size());
    }



}
