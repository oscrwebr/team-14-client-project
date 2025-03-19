package com.team14.clientProject.addApplicant;

import com.team14.clientProject.homePage.Applicants;
import com.team14.clientProject.loggingSystem.CommunicationLogRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.team14.clientProject.loggingSystem.CommunicationLogRepository;

import java.util.List;

@Repository
public class AddApplicantRepositoryImpl implements AddApplicantRepository {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<Applicants> ApplicantRowMapper;
    @Autowired
    private CommunicationLogRepositoryImpl communicationLogRepository;

    public AddApplicantRepositoryImpl(JdbcTemplate aJdbc) {
        this.jdbcTemplate = aJdbc;
        setApplicantRowMapper();
    }

    // Row mapper for the applicant object
    private void setApplicantRowMapper() {
        ApplicantRowMapper = (rs, rowNum) -> {
            Applicants applicant = new Applicants(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("location"),
                    rs.getString("email"),
                    rs.getString("phoneNumber"),
                    rs.getString("eventAttended"),
                    rs.getString("skill"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt")
            );
            return applicant;
        };
    }

    public Integer emailValidation(AddApplicantForm applicants) {
        String emailValidation =
                "select count(*) " +
                "from applicants " +
                "where email = ?";

        Integer queryResultEmail = jdbcTemplate.queryForObject(emailValidation, Integer.class, applicants.getEmail());
        return queryResultEmail;
    }

    public Integer phoneNoValidation(AddApplicantForm applicants) {
        String phoneNoValidation =
                "select count(*) " +
                "from applicants " +
                "where phoneNumber = ?";
        Integer queryResultPhone = jdbcTemplate.queryForObject(phoneNoValidation, Integer.class, applicants.getPhoneNumber());
        return queryResultPhone;
    }

    public Integer getApplicantCount() {
        String sql =
                "select count(*) " +
                "from " +
                "applicants";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void addApplicant(AddApplicantForm applicants) {

        // Insert query for the form data
        String sql =
                "insert into applicants" +
                "(firstName, lastName, location, email, phoneNumber, eventAttended, skill) " +
                "values (?, ?, ?, ?, ?, ?, ?)";

        // Values for the query
        jdbcTemplate.update(sql,
                applicants.getFirstName(),
                applicants.getLastName(),
                applicants.getLocation(),
                applicants.getEmail(),
                applicants.getPhoneNumber(),
                applicants.getEventAttended(),
                applicants.getSkill()
        );
        this.communicationLogRepository.addApplicantLog();

    }

    public Integer getRecentId() {
        String fetchId = "select LAST_INSERT_ID()";
        Integer id = jdbcTemplate.queryForObject(fetchId, Integer.class);
        return id;
    }


    public void addApplicantFromCsv(Applicant applicant) {
        String sql =
                "insert into applicants" +
                "(firstName, lastName, location, email, phoneNumber, eventAttended, skill) " +
                "values (?, ?, ?, ?, ?, ?, ?)";

//        String fetchId = "SELECT id FROM applicants WHERE email = ?";

        // Fetch the id of the applicant that was just inserted
//        String fetchId = "SELECT LAST_INSERT_ID()";
        jdbcTemplate.update(sql,
                applicant.getFirstName(),
                applicant.getLastName(),
                applicant.getLocation(),
                applicant.getEmail(),
                applicant.getPhoneNumber(),
                applicant.getEventAttended(),
                applicant.getSkill()
        );
//        int id = jdbcTemplate.queryForObject(fetchId, Integer.class);
//        return id;
    }

    public void addApplicantPreferencesFromCsv(ApplicantPreferences applicantPreferences, Integer id) {
        String sql =
                "insert into applicantpreferences" +
                "(applicationId, SubscribeToNewsLetter, SubscribeToBulletins, SubscribeToJobUpdates) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                id,
                applicantPreferences.getSubscribeToNewsletter(),
                applicantPreferences.getSubscribeToBulletins(),
                applicantPreferences.getSubscribeToJobUpdates()
        );
    }

    public void addApplicantDetailsFromCsv(ApplicantDetails applicantDetails, Integer id) {
        String sql =
                "insert into applicationdetails" +
                "(applicationId, currentPosition, status, CvPath, CoverLetterPath) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                id,
                applicantDetails.getCurrentPosition(),
                applicantDetails.getStatus(),
                applicantDetails.getCvPath(),
                applicantDetails.getCoverLetterPath()
        );
    }


}
