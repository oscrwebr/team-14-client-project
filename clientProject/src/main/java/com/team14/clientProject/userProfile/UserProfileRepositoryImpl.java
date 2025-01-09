package com.team14.clientProject.userProfile;
import com.team14.clientProject.homePage.Applicants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private RowMapper<User> userRowMapper;
    private JdbcTemplate jdbcTemplate;
    public UserProfileRepositoryImpl(JdbcTemplate aJdbc) {
        this.jdbcTemplate = aJdbc;
        setRowMapper();

    }



    private void setRowMapper() {
        userRowMapper = (rs, rowNum) -> {
            User user = new User(
                        rs.getString("username"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("role")
            );
            return user;
        };
    }
    // Username acts as the ID
    public User getDataFromUsername(String username) {
        String query = "SELECT username, firstName, lastName, role FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(query, userRowMapper, username);
    }

}
