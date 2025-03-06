package com.team14.clientProject.profilePage;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;



@Repository
public class ProfilePageRepositoryImpl implements ProfilePageRepository {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<Profile> ProfileRowMapper;



    public ProfilePageRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createProfileRowMapper();
    }


    private void createProfileRowMapper() {
        ProfileRowMapper = (rs, rowNum) -> {
            Profile profile = new Profile(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("location"),
                    rs.getString("email"),
                    rs.getString("phoneNumber"),
                    rs.getString("eventAttended"),
                    rs.getString("skill")
            );

            applicantPreferences preferences = new applicantPreferences(
                    rs.getInt("id"),
                    "Yes".equals(rs.getString("SubscribeToNewsLetter")),
                    "Yes".equals(rs.getString("SubscribeToBulletins")),
                    "Yes".equals(rs.getString("SubscribeToJobUpdates"))
            );
            profile.setPreferences(preferences);

            applicantJobDetails jobDetails = new applicantJobDetails(
                    rs.getInt("id"),
                    rs.getString("currentPosition"),
                    rs.getString("status")
            );
            profile.setJobDetails(jobDetails);

            return profile;
        };
    }


    @Override
    public List<Profile> getProfiles(){
        String sql =  "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id";

        return jdbcTemplate.query(sql, ProfileRowMapper);
    }

    @Override
    public Profile getProfileById(int id) {
        // SQL query to fetch profile and preferences for a specific applicant
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "WHERE a.id = ?";
        return jdbcTemplate.queryForObject(sql, ProfileRowMapper, id);
    }
    public void addProfile(Profile profile) {
        String sql = "insert into applicants (firstName, lastName, location, email, phoneNumber, eventAttended, skill) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, profile.getFirstName(), profile.getLastName(), profile.getLocation(), profile.getEmail(), profile.getPhoneNumber(), profile.getEventAttended(), profile.getSkill());
    }
    @Override
    public List<Profile> getProfilesByFirstNameAscending() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "ORDER BY a.firstName ASC";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByFirstNameDescending() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "ORDER BY a.firstName DESC";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByLastNameAscending() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "ORDER BY a.lastName ASC";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByLastNameDescending() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "ORDER BY a.lastName DESC";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<Profile> getProfilesByUniqueLocation() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "GROUP BY a.location";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<Profile> getProfilesByUniqueEvent() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "GROUP BY a.eventAttended";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<String> getUniqueSkills() {
        String sql = "SELECT DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(a.skill, ',', numbers.n), ',', -1)) AS skill " +
                "FROM applicants a " +
                "JOIN (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) numbers " +
                "ON CHAR_LENGTH(a.skill) - CHAR_LENGTH(REPLACE(a.skill, ',', '')) >= numbers.n - 1 " +
                "ORDER BY skill";
        return jdbcTemplate.queryForList(sql, String.class);
    }



    public void updateCvPath(int userId, byte[] cvPath) {
        String sql = "UPDATE applicationDetails SET cvPath = ? WHERE id = ?";
        jdbcTemplate.update(sql, cvPath, userId);
    }

    public byte[] getCvPath(int userId) {
        String sql = "SELECT cvPath FROM applicationDetails WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, userId);
    }

    public List<Profile> searchProfiles(String query) {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates, " +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantPreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationDetails d ON a.Id = d.Id " +
                "WHERE a.firstName LIKE ? OR a.lastName LIKE ? OR a.location LIKE ? OR a.skill LIKE ? OR a.eventAttended LIKE ? OR d.currentPosition LIKE ?";
        String searchQuery = "%" + query + "%";
        return jdbcTemplate.query(sql, new Object[]{searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery}, ProfileRowMapper);
    }

    @Override
    public void updateProfile(Profile profile) {
        String sql = "UPDATE applicants SET firstName = ?, lastName = ?, location = ?, email = ?, phoneNumber = ?, eventAttended = ?, skill = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                profile.getFirstName(),
                profile.getLastName(),
                profile.getLocation(),
                profile.getEmail(),
                profile.getPhoneNumber(),
                profile.getEventAttended(),
                profile.getSkill(),
                profile.getId());

        String jobDetailsSql = "UPDATE applicationdetails SET currentPosition = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(jobDetailsSql, profile.getJobDetails().getCurrentPosition(), profile.getJobDetails().getStatus(), profile.getId());

        String preferencesSql = "UPDATE applicantPreferences SET SubscribeToNewsLetter = ?, SubscribeToBulletins = ?, SubscribeToJobUpdates = ? WHERE applicationId = ?";
        jdbcTemplate.update(preferencesSql,
                profile.getPreferences().isSubscribeToNewsletter() ? "Yes" : "No",
                profile.getPreferences().isSubscribeToBulletins() ? "Yes" : "No",
                profile.getPreferences().isSubscribeToJobUpdates() ? "Yes" : "No",
                profile.getId());
    }

    public void deleteProfile(int id) {
        String insertSql = "INSERT INTO deletedApplicants (id, firstName, lastName, location, email, phoneNumber, currentPosition, status, skill, eventAttended, SubscribeToNewsLetter, SubscribeToBulletins, SubscribeToJobUpdates) " +
                "SELECT a.id, a.firstName, a.lastName, a.location, a.email, a.phoneNumber, " +
                "d.currentPosition, d.status, a.skill, a.eventAttended, " +
                "p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.applicationId " +
                "LEFT JOIN applicationdetails d ON a.Id = d.applicationId " +
                "WHERE a.id = ?";
        jdbcTemplate.update(insertSql, id);

        String sql = "DELETE FROM applicants WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<String> getSubscribedEmails(){
        String sql = "SELECT a.email " +
                "FROM applicants a " +
                "JOIN applicantPreferences p ON a.Id = p.Id " +
                "WHERE p.SubscribeToNewsLetter = 'yes'";

        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getString("email"));
    }

}

