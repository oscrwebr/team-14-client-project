package com.team14.clientProject.profilePage;

import com.team14.clientProject.automaticDeletion.ProfileListUpdatedEvent;
import com.team14.clientProject.loggingSystem.CommunicationLogRepository;
import com.team14.clientProject.loggingSystem.CommunicationLog;
import com.team14.clientProject.loggingSystem.CommunicationLogRepositoryImpl;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.ui.Model;
import com.team14.clientProject.emailPage.mail.EmailService;
import com.team14.clientProject.emailPage.mail.EmailValidation;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfilePage {

    @Autowired
    private CommunicationLogRepositoryImpl CommunicationLogRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ProfilePageRepositoryImpl profilePageRepository;
    private CommunicationLogRepository communicationLogRepository;
    private List<CommunicationLog> communicationLogList;
    private List<Profile> profileList;
    @Autowired
    private EmailService emailService;

    // Modifying profileList when the ProfileListUpdatedEvent is published
    @EventListener
    public void handleProfileListUpdatedEvent(ProfileListUpdatedEvent event) {
        this.profileList = profilePageRepository.getProfiles();
    }


    public ProfilePage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.profilePageRepository = new ProfilePageRepositoryImpl(jdbcTemplate);
        this.profileList = profilePageRepository.getProfiles();
        this.communicationLogRepository = new CommunicationLogRepositoryImpl(jdbcTemplate);
    }

    @GetMapping()
    public ModelAndView profilePage() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        modelAndView.addObject("profileList", this.profileList);
        modelAndView.addObject("uniqueLocations", this.profilePageRepository.getProfilesByUniqueLocation());
        modelAndView.addObject("uniqueEvents", this.profilePageRepository.getProfilesByUniqueEvent());
        modelAndView.addObject("uniqueSkills", this.profilePageRepository.getUniqueSkills());
        return modelAndView;
    }

    @GetMapping("/{applicantId}")
    public ModelAndView profilePage(@PathVariable Integer applicantId) {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        if (this.profilePageRepository.getProfileById(applicantId) == null) {
            //throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found");
            return new ModelAndView("redirect:/profile");
        } else {
            Profile profile = this.profilePageRepository.getProfileById(applicantId);
            modelAndView.addObject("profile", profile);
            System.out.println("Applicant ID: " + applicantId);
            modelAndView.addObject("Log", this.communicationLogRepository.getLogsByApplicantId(applicantId));


            //Displaying the cv if it exists

            byte[] cvPath = profilePageRepository.getCvPath(applicantId);
            if(cvPath != null){
                String cvBase64 = Base64.getEncoder().encodeToString(cvPath);
                modelAndView.addObject("cvBase64", cvBase64);
            }
            return modelAndView;
        }
    }
    @GetMapping("/firstNameAscending")
    public ModelAndView getProfilesByFirstNameAscending() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        List<Profile> profileList = this.profilePageRepository.getProfilesByFirstNameAscending();
        modelAndView.addObject("profileList", profileList);
        return modelAndView;
    }
    @GetMapping("/firstNameDescending")
    public ModelAndView getProfilesByFirstNameDescending() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        List<Profile> profileList = this.profilePageRepository.getProfilesByFirstNameDescending();
        modelAndView.addObject("profileList", profileList);
        return modelAndView;
    }
    @GetMapping("/lastNameAscending")
    public ModelAndView getProfilesByLastNameAscending() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        List<Profile> profileList = this.profilePageRepository.getProfilesByLastNameAscending();
        modelAndView.addObject("profileList", profileList);
        return modelAndView;
    }
    @GetMapping("/lastNameDescending")
    public ModelAndView getProfilesByLastNameDescending() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        List<Profile> profileList = this.profilePageRepository.getProfilesByLastNameDescending();
        modelAndView.addObject("profileList", profileList);
        return modelAndView;
    }
    @GetMapping("/location")
    public ModelAndView getProfilesByLocation() {
        ModelAndView modelAndView = new ModelAndView("profilePage");
        List<Profile> profileList = this.profilePageRepository.getProfiles();
        modelAndView.addObject("profileList", profileList);
        modelAndView.addObject("uniqueLocations", this.profilePageRepository.getProfilesByUniqueLocation());
        return modelAndView;
    }


    @PostMapping("/sendEmail/{userID}")
    public ModelAndView sendEmail(@PathVariable String userID) throws MessagingException {
        // Query to fetch the email address of the applicant based on userID
        String query = "SELECT email FROM applicants WHERE id = ?";
        String emailAddress = jdbcTemplate.queryForObject(query, new Object[]{userID}, String.class);

        // Initialize the ModelAndView object with the profilePage view
        ModelAndView modelAndView = new ModelAndView("profilePage");

        // Check if the email address is null and set an alert message if true
        if (emailAddress == null) {
            modelAndView.addObject("alertMessage", "Email not found for user ID " + userID);
            return modelAndView;
        }

        // Define the regex pattern for email validation
        String regexPattern = "^[a-zA-Z0-9_!#$%&*+/=?`{}~^.-]+@[a-zA-Z0-9.-]+$";
        // Validate the email address format and set an alert message if invalid
        if (!EmailValidation.patternMatches(emailAddress, regexPattern)) {
            modelAndView.addObject("alertMessage", "Invalid email format for user ID " + userID);
            return modelAndView;
        }

        // Define the email subject and HTML body content
        String subject = "Test Subject to Your Specific Email";
        String htmlBody = "<html><body><h1>An email has been sent to your specific email address. </h1><img src='cid:logo'></body></html>";
        String logoPath = "src/main/resources/static/images/dhcw.png";

        // Send the email with the specified subject, body, and logo
        emailService.sendHtmlMessageWithLogo(emailAddress, subject, htmlBody, logoPath);
        // Set a success alert message
        modelAndView.addObject("alertMessage", "Email sent successfully to " + emailAddress);
        return modelAndView;
    }

    @PostMapping("/uploadCV/{userID}")
    public String uploadCv(@PathVariable int userID, @RequestParam("cvUpload") MultipartFile file, Model model){
        if(file.isEmpty()){
            model.addAttribute("message", "Please select a file to upload");
            return "profilePage";
        }
        try{
            if(!file.getContentType().equals("application/pdf")){
                model.addAttribute("message", "Please upload a PDF file");
                return "profilePage";
            }

            byte[] cvPath = file.getBytes();
            profilePageRepository.updateCvPath(userID, cvPath);

            String cvURL = "/profile/viewCV/" + userID;

            model.addAttribute("cvURL", cvURL);
            model.addAttribute("message", "File uploaded successfully");
        } catch (Exception e){
            model.addAttribute("message", "An error occurred while uploading the file");
        }
        return "profilePage";
    }

    @GetMapping("/search")
    public ModelAndView searchProfiles(@RequestParam("query") String query, Model model) {
        List<Profile> searchResults = profilePageRepository.searchProfiles(query);
        ModelAndView modelAndView = new ModelAndView("profilePage");
        modelAndView.addObject("profileList", searchResults);
        modelAndView.addObject("uniqueLocations", this.profilePageRepository.getProfilesByUniqueLocation());
        if (searchResults.isEmpty()) {
            modelAndView.addObject("alertMessage", "No results found");
        }
        return modelAndView;
    }

    @GetMapping("/viewCV/{userID}")
    public ResponseEntity<byte[]> viewCv(@PathVariable int userID) {
        try{
            byte[] cvFile = profilePageRepository.getCvPath(userID);

            if (cvFile == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CV not found");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("CV.pdf").build());

            return new ResponseEntity<>(cvFile, headers, HttpStatus.OK);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching the CV", e);
        }
    }

    @PostMapping("/edit/{userID}")
    public String editProfile(@PathVariable int userID,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String location,
                              @RequestParam String email,
                              @RequestParam String phoneNumber,
                              @RequestParam String currentPosition,
                              @RequestParam String status,
                              @RequestParam String skill,
                              @RequestParam String eventAttended,
                              @RequestParam(required = false) Boolean subscribeToNewsletter,
                              @RequestParam(required = false) Boolean subscribeToBulletins,
                              @RequestParam(required = false) Boolean subscribeToJobUpdates,
                              Model model) {
        Profile profile = profilePageRepository.getProfileById(userID);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setLocation(location);
        profile.setEmail(email);
        profile.setPhoneNumber(phoneNumber);
        profile.setEventAttended(eventAttended);
        profile.setSkill(skill);
        profile.setCurrentPosition(currentPosition);
        profile.setStatus(status);

        applicantPreferences preferences = profile.getPreferences();

            preferences.setSubscribeToNewsletter(subscribeToNewsletter != null && subscribeToNewsletter);
            preferences.setSubscribeToBulletins(subscribeToBulletins != null && subscribeToBulletins);
            preferences.setSubscribeToJobUpdates(subscribeToJobUpdates != null && subscribeToJobUpdates);

        profile.setPreferences(preferences);

        profilePageRepository.updateProfile(profile);
        this.communicationLogRepository.editApplicantLog(userID);

        this.profileList = profilePageRepository.getProfiles();

        model.addAttribute("profile", profile);
        return "profilePage";
    }
    @PostMapping("/delete/{userID}")
    public String deleteProfile(@PathVariable int userID, RedirectAttributes redirectAttributes) {
        this.communicationLogRepository.deleteApplicantLog(userID);
        Profile profile = profilePageRepository.getProfileById(userID);

        profilePageRepository.deleteProfile(userID);
        this.profileList = profilePageRepository.getProfiles();
        redirectAttributes.addFlashAttribute("alertMessage", "Profile deleted successfully");
        return "redirect:/profile";
    }

}

