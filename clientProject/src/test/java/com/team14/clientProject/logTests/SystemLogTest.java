package com.team14.clientProject.logTests;

import com.team14.clientProject.loggingSystem.SystemLogRepositoryImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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


    /*
------------------------------------------------FRONTEND------------------------------------------------
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAllSystemLogsShowOnAdminPage() throws Exception {
        MvcResult result = mvc
                .perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document doc = Jsoup.parse(content);
        Elements logsShownOnPage = doc.select(".SystemLogs");
        assertEquals(systemLogRepositoryImpl.getLogs().size(), logsShownOnPage.size());
    }

}
