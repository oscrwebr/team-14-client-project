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
        String sql =  "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id";

        return jdbcTemplate.query(sql, ProfileRowMapper);
    }

    @Override
    public Profile getProfileById(int id) {
        // SQL query to fetch profile and preferences for a specific applicant
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id" +
                "where a.id = ?";
        return jdbcTemplate.queryForObject(sql, ProfileRowMapper, id);
    }
    public void addProfile(Profile profile) {
        String sql = "insert into applicants (firstName, lastName, location, email, phoneNumber, eventAttended, skill) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, profile.getFirstName(), profile.getLastName(), profile.getLocation(), profile.getEmail(), profile.getPhoneNumber(), profile.getEventAttended(), profile.getSkill());
    }
    @Override
    public List<Profile> getProfilesByFirstNameAscending() {
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id" +
                "order by a.firstName asc";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByFirstNameDescending() {
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
        "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id" +
                "order by a.firstName desc";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByLastNameAscending() {
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id" +
                "order by a.lastName asc";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    @Override
    public List<Profile> getProfilesByLastNameDescending() {
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id" +
                "order by a.lastName desc";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<Profile> getProfilesByUniqueLocation() {
        String sql = "select a.*, p.aubscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id " +
                "group by a.location";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<Profile> getProfilesByUniqueEvent() {
        String sql = "select a.*, p.aubscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id " +
                "group by a.eventAttended";
        return jdbcTemplate.query(sql, ProfileRowMapper);
    }
    public List<String> getUniqueSkills() {
        String sql = "select distinct trim(substring_index(substring_index(a.skill, ',', numbers.n), ',', -1)) as skill " +
                "from applicants a " +
                "join (select 1 n union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9 union all select 10) numbers " +
                "on char_length(a.skill) - char_length(replace(a.skill, ',', '')) >= numbers.n - 1 " +
                "ORDER BY skill";
        return jdbcTemplate.queryForList(sql, String.class);
    }



    public void updateCvPath(int userId, byte[] cvPath) {
        String sql = "update applicationdetails set cvPath = ? where id = ?";
        jdbcTemplate.update(sql, cvPath, userId);
    }

    public byte[] getCvPath(int userId) {
        String sql = "select cvPath from applicationdetails where id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, userId);
    }

    public List<Profile> searchProfiles(String query) {
        String sql = "select a.*, p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates, " +
                "d.currentPosition, d.status " +
                "from applicants a " +
                "left join applicantpreferences p on a.id = p.id " +
                "left join applicationdetails d on a.id = d.id " +
                "where a.firstName like ? or a.lastName like ? or a.location like ? or a.skill like ? or a.eventAttended like ? or d.currentPosition like ?";
        String searchQuery = "%" + query + "%";
        return jdbcTemplate.query(sql, new Object[]{searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery}, ProfileRowMapper);
    }

    @Override
    public void updateProfile(Profile profile) {
        String sql = "update applicants set firstName = ?, lastName = ?, location = ?, email = ?, phoneNumber = ?, eventAttended = ?, skill = ? where id = ?";
        jdbcTemplate.update(sql,
                profile.getFirstName(),
                profile.getLastName(),
                profile.getLocation(),
                profile.getEmail(),
                profile.getPhoneNumber(),
                profile.getEventAttended(),
                profile.getSkill(),
                profile.getId());

        String jobDetailsSql = "update applicationdetails set currentPosition = ?, status = ? where id = ?";
        jdbcTemplate.update(jobDetailsSql, profile.getJobDetails().getCurrentPosition(), profile.getJobDetails().getStatus(), profile.getId());

        String preferencesSql = "update applicantPreferences set subscribeToNewsLetter = ?, subscribeToBulletins = ?, subscribeToJobUpdates = ? where applicationId = ?";
        jdbcTemplate.update(preferencesSql,
                profile.getPreferences().isSubscribeToNewsletter() ? "Yes" : "No",
                profile.getPreferences().isSubscribeToBulletins() ? "Yes" : "No",
                profile.getPreferences().isSubscribeToJobUpdates() ? "Yes" : "No",
                profile.getId());
    }

    public void deleteProfile(int id) {
        String insertSql = "insert into deletedApplicants (id, firstName, lastName, location, email, phoneNumber, currentPosition, status, skill, eventAttended, subscribeToNewsLetter, subscribeToBulletins, subscribeToJobUpdates) " +
                "select a.id, a.firstName, a.lastName, a.location, a.email, a.phoneNumber, " +
                "d.currentPosition, d.status, a.skill, a.eventAttended, " +
                "p.subscribeToNewsLetter, p.subscribeToBulletins, p.subscribeToJobUpdates " +
                "from applicants a " +
                "left join applicantpreferences p ON a.id = p.applicationId " +
                "left join applicationdetails d ON a.id = d.applicationId " +
                "where a.id = ?";
        jdbcTemplate.update(insertSql, id);

        String sql = "delete from applicants where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<String> getSubscribedEmails(){
        String sql = "select a.email " +
                "from applicants a " +
                "join applicantpreferences p on a.id = p.id " +
                "where p.subscribeToNewsLetter = 'yes'";
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getString("email"));
    }

}

