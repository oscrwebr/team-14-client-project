package com.team14.clientProject.logTests;

import com.team14.clientProject.loggingSystem.CommunicationLog;
import com.team14.clientProject.loggingSystem.CommunicationLogRepositoryImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CommunicationLogTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CommunicationLogRepositoryImpl communicationLogRepositoryImpl;

    @BeforeEach
    void setUp() {
        communicationLogRepositoryImpl.clearLogs();
        System.out.println("communicationLogRepositoryImpl.getLogs().size() = " + communicationLogRepositoryImpl.getLogs().size());
    }

    @Test
    void testCommunicationLogRepositoryImpl() {
        assertNotNull(communicationLogRepositoryImpl, "CommunicationLogRepositoryImpl should not be null");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAddEmailLog() {
        List<String> userIds = List.of("1");
        String notes = "testing123";

        communicationLogRepositoryImpl.addEmailLog(userIds, notes);

        List<CommunicationLog> logs = communicationLogRepositoryImpl.getLogs();
        assertEquals(1, logs.size(), "Log size should be 1 after adding a log");
        assertEquals(notes, logs.get(0).getNotes(), "Log notes should match the input");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAddApplicant() {
        communicationLogRepositoryImpl.addApplicantLog();

        List<CommunicationLog> logs = communicationLogRepositoryImpl.getLogs();
        assertEquals(1, logs.size(), "Log size should be 1 after adding an applicant log");
        assertEquals("Applicant added to the system", logs.get(0).getNotes(), "Notes should indicate applicant addition");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testEditApplicantLog() {
        assertTrue(communicationLogRepositoryImpl.getLogs().isEmpty(), "Log list should be empty before adding a log");
        communicationLogRepositoryImpl.editApplicantLog(1);
        List<CommunicationLog> logs = communicationLogRepositoryImpl.getLogs();
        assertEquals(1, logs.size(), "Log size should still be 1 after editing");
        assertEquals("Applicant details edited", logs.get(0).getNotes(), "Notes should indicate applicant editing");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteApplicantLog() {
        assertTrue(communicationLogRepositoryImpl.getLogs().isEmpty(), "Log list should be empty before adding a log");
        communicationLogRepositoryImpl.deleteApplicantLog(1);
        List<CommunicationLog> logs = communicationLogRepositoryImpl.getLogs();
        assertEquals(1, logs.size(), "Log size should still be 1 after deletion");
        assertEquals("Applicant deleted from the system", logs.get(0).getNotes(), "Notes should indicate applicant deletion");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getLogId() {
        communicationLogRepositoryImpl.addApplicantLog();
        int expectedLogId = 8;
        assertEquals(expectedLogId, communicationLogRepositoryImpl.getLogs().get(0).getLogId(), "Log ID should match");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getApplicantId() {
        communicationLogRepositoryImpl.addApplicantLog();
        int expectedApplicantId = 87;
        assertEquals(expectedApplicantId, communicationLogRepositoryImpl.getLogs().get(0).getApplicantId(), "Applicant ID should match");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUserId() {
        communicationLogRepositoryImpl.addEmailLog(List.of("2"), "testing");
        String expectedUserId = "1"; // admin user has an id of 1
        assertEquals(expectedUserId, communicationLogRepositoryImpl.getLogs().get(0).getUserId(), "User ID should match");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getNotes() {
        String expectedNotes = "Invitation to interview";
        communicationLogRepositoryImpl.addEmailLog(List.of("1"), expectedNotes);
        assertEquals(expectedNotes, communicationLogRepositoryImpl.getLogs().get(0).getNotes(), "Notes should match");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testLogTableShowsOnAdminPage() throws Exception {
        MvcResult result = mvc
                .perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logTable = doc.select(".logTable");
        assertNotNull(logTable, "Log table should be present on the admin page");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAllCommunicationLogsShowOnAdminPage() throws Exception {
        communicationLogRepositoryImpl.addApplicantLog();

        MvcResult result = mvc
                .perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logsShownOnPage = doc.select(".communicationLog");

        assertEquals(communicationLogRepositoryImpl.getLogs().size(), logsShownOnPage.size(), "All communication logs should be displayed on the admin page");
    }
}
