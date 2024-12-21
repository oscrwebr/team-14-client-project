package com.team14.clientProject.addCsv;

import com.team14.clientProject.addApplicant.AddApplicantRepository;
import com.team14.clientProject.addApplicant.AddApplicantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddCsvTests {
    // https://www.infoworld.com/article/2257877/junit-5-tutorial-part-2-unit-testing-spring-mvc-with-junit-5.html
    @Autowired
    private AddApplicantService addApplicantService;

    @Autowired
    private AddApplicantRepository addApplicantRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mock/web/MockMultipartFile.html
    public void uploadNonCsvFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "csv",
                "csv.pdf",
                // Content type for PDF: https://stackoverflow.com/questions/312230/proper-mime-media-type-for-pdf-files
                "application/pdf",
                "PDF File".getBytes());

        MvcResult result = mvc.perform(multipart("/add-applicant/csv")
                        .file(file)
                        .with(csrf()))
                        .andReturn();


        String content = result.getResponse().getContentAsString();
        System.out.println("Content is"+content);
        assertTrue(content.contains("<p>Invalid file type</p>"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUploadCsvFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "csv",
                "csv.csv",
                //https://developer.mozilla.org/en-US/docs/Web/HTTP/MIME_types/Common_types
                "text/csv",
                "CSV File".getBytes());

        MvcResult result = mvc.perform(multipart("/add-applicant/csv")
                        .file(file)
                        .with(csrf()))
                        .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println(content);
        assertTrue(content.contains("<p>CSV file successfully inserted</p>"));
    }

//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void testCsvCorrectlyInsertsApplicants() throws Exception {
//        String mockContent = "firstName,lastName,location,email,phoneNumber,eventAttended,skill,SubscribeToNewsletter,SubscribeToBulletins,SubscribeToJobUpdates,currentPosition,status,CvPath,CoverLetterPath\n" +
//                "John,Doe,New York,john.doe@example.com,07111222333,Job Fair,Java Developer,Yes,No,No,Senior Developer,External,,";
//
//        MockMultipartFile file = new MockMultipartFile(
//                "csv",
//                "csv.csv",
//                "text/csv",
//                mockContent.getBytes());
//
//        mvc.perform(multipart("/add-applicant/csv")
//                        .file(file)
//                        .with(csrf()))
//                        .andReturn();
//
//        // Database configured to have 87 applicants when running, so when an applicant is added there will always be an 88
//        Integer totalApplicants = addApplicantRepository.getApplicantCount();
//        assertEquals(88, totalApplicants);
//
//
//
//
//    }

}

