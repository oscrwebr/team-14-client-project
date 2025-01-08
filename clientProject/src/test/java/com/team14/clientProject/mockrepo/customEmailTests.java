package com.team14.clientProject.mockrepo;

import com.team14.clientProject.emailPage.EmailPage;
import com.team14.clientProject.emailPage.mail.EmailServiceHandler;
import com.team14.clientProject.profilePage.Profile;
import com.team14.clientProject.profilePage.ProfilePageRepositoryImpl;
import com.team14.clientProject.loggingSystem.CommunicationLogRepositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.servlet.ModelAndView;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@AutoConfigureMockMvc
public class customEmailTests {

    @Mock
    private EmailServiceHandler emailServiceHandler;

    @Mock
    private ProfilePageRepositoryImpl profilePageRepository;

    @Mock
    private CommunicationLogRepositoryImpl communicationLogRepository;

    @InjectMocks
    private EmailPage emailPage;

    private List<Profile> mockProfiles;

    // Setting up the test environment
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock dependencies
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        profilePageRepository = mock(ProfilePageRepositoryImpl.class);
        CommunicationLogRepositoryImpl communicationLogRepository = mock(CommunicationLogRepositoryImpl.class);

        // Mocking profiles
        mockProfiles = Arrays.asList(
                new Profile(1, "John", "Doe", "New York", "john.doe@example.com", "1234567890", "Event1", "Skill1"),
                new Profile(2, "Jane", "Smith", "Los Angeles", "jane.smith@example.com", "0987654321", "Event2", "Skill2")
        );

        // Mocking the repositories behavior
        when(profilePageRepository.getProfiles()).thenReturn(mockProfiles);

        // Initialize EmailPage with mocked dependencies
        emailPage = new EmailPage(jdbcTemplate, profilePageRepository, communicationLogRepository);


        try {
            Field emailServiceHandlerField = EmailPage.class.getDeclaredField("emailServiceHandler");
            emailServiceHandlerField.setAccessible(true);
            emailServiceHandlerField.set(emailPage, emailServiceHandler);

            Field profileListField = EmailPage.class.getDeclaredField("profileList");
            profileListField.setAccessible(true);
            profileListField.set(emailPage, mockProfiles);
            Field communicationLogRepositoryField = EmailPage.class.getDeclaredField("CommunicationLogRepository");
            communicationLogRepositoryField.setAccessible(true);
            communicationLogRepositoryField.set(emailPage, communicationLogRepository);


        } catch (Exception e) {
            fail("Failed to inject dependencies: " + e.getMessage());
        }
    }



    // Test for sending emails to recipients
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testSendCustomEmails_WithRecipients_Success() {

        List<String> emailIds = Arrays.asList("john.doe@example.com", "jane.smith@example.com");
        String successMessage = "Emails sent successfully.";
        when(emailServiceHandler.sendEmails(emailIds, "Test Subject", "Test Message", ""))
                .thenReturn(successMessage);

        ModelAndView result = emailPage.sendCustomEmails("Test Subject", "Test Message", emailIds);

        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("alertMessage"));
        assertEquals(successMessage, result.getModel().get("alertMessage"));
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }


    // Test for when the SMTP server is not reachable
    @Test
    public void testSendCustomEmails_WithRecipients_Failure() {

        List<String> emailIds = Arrays.asList("john.doe@example.com", "jane.smith@example.com");
        when(emailServiceHandler.sendEmails(emailIds, "Test Subject", "Test Message", ""))
                .thenThrow(new RuntimeException("SMTP server not reachable."));


        ModelAndView result = emailPage.sendCustomEmails("Test Subject", "Test Message", emailIds);


        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("alertMessage"));
        assertEquals("Error sending emails: SMTP server not reachable.", result.getModel().get("alertMessage"));
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }


    // Test for when the subject is empty
    @Test
    public void testCustomComposePage() {

        ModelAndView result = emailPage.customComposePage();


        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }


    // Test for when no recipients are selected
    @Test
    public void testSendCustomEmails_NoRecipients() {

        ModelAndView result = emailPage.sendCustomEmails("Test Subject", "Test Message", null);

        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("alertMessage"));
        assertEquals("Please select at least one recipient.", result.getModel().get("alertMessage"));
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }


    // Test for when the subject is empty
    @Test
    public void testSendCustomEmails_EmptySubject() {
        List<String> emailIds = Arrays.asList("john.doe@example.com", "jane.smith@example.com");

        ModelAndView result = emailPage.sendCustomEmails("", "Test Message", emailIds);

        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("alertMessage"));
        assertEquals("Subject cannot be empty.", result.getModel().get("alertMessage"));
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }


    // Test for empty message
    @Test
    public void testSendCustomEmails_EmptyMessage() {
        List<String> emailIds = Arrays.asList("john.doe@example.com", "jane.smith@example.com");

        ModelAndView result = emailPage.sendCustomEmails("Test Subject", "", emailIds);

        assertEquals("email/customEmails", result.getViewName());
        assertTrue(result.getModel().containsKey("alertMessage"));
        assertEquals("Message cannot be empty.", result.getModel().get("alertMessage"));
        assertTrue(result.getModel().containsKey("profileList"));
        assertEquals(mockProfiles, result.getModel().get("profileList"));
    }
}
