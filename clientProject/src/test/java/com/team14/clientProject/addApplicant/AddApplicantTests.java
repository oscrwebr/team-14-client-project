package com.team14.clientProject.addApplicant;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AddApplicantTests {


    @Autowired
    private AddApplicantService addApplicantService;

    @Autowired
    private AddApplicantRepository addApplicantRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    // Database is configured to always have 87 applicants when running the application
    public void shouldGetEightyEightApplicantsAfterAddingAnApplicant() throws Exception {
        // Arrange
        AddApplicantForm mockForm = new AddApplicantForm();
        mockForm.setFirstName("John");
        mockForm.setLastName("Doe");
        mockForm.setEmail("johndoe@example.com");
        mockForm.setLocation("Cardiff");
        mockForm.setPhoneNumber("07111222333");
        mockForm.setEventAttended("NHS Careers Fair");
        mockForm.setSkill("Skill");

        // Act
        addApplicantService.addApplicant(mockForm);
        Integer totalApplicants = addApplicantRepository.getApplicantCount();
        // Assert
        assertEquals(88, totalApplicants);

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void successMessageDisplayedAfterApplicantIsAdded() throws Exception {
        AddApplicantForm mockForm = new AddApplicantForm();
        mockForm.setFirstName("John");
        mockForm.setLastName("Doe");
        mockForm.setLocation("Cardiff");
        mockForm.setEmail("johndoe2@example.com");
        mockForm.setPhoneNumber("07111222333");
        mockForm.setEventAttended("NHS Careers Fair");
        mockForm.setSkill("Skill");

        MvcResult when = mvc
                .perform(post("/add-applicant/manual")
                        .with(csrf())
                        .param("firstName", mockForm.getFirstName())
                        .param("lastName", mockForm.getLastName())
                        .param("location", mockForm.getLocation())
                        .param("email", mockForm.getEmail())
                        .param("phoneNumber", mockForm.getPhoneNumber())
                        .param("eventAttended", mockForm.getEventAttended())
                        .param("skill", mockForm.getSkill()))
                        .andReturn();


        String whenContent = when.getResponse().getContentAsString();

//        System.out.println("THIS IS THE WHEN CONTENT"+whenContent);

        //addApplicantService.addApplicant(mockForm);

//        MvcResult then = mvc
//                .perform(get("/add-applicant/manual"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();

//        System.out.println("THIS IS THE RESPONSE FROM THE GET.");



        String content = when.getResponse().getContentAsString();

        System.out.println("THIS IS THE CONTENT"+content);

        assertTrue(content.contains("<p>Applicant added successfully</p>"));

    }



}
