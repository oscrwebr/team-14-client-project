package com.team14.clientProject.adminPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userMapper;
    @Autowired


    // Constructor injection of JdbcTemplate
    public AdminRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setUserMapper();
    }

    // Set up the row mapper for the User object
    private void setUserMapper() {
        userMapper = (rs, rowNum) -> new User(
                rs.getInt("ID"),
                rs.getString("username"),
                rs.getString("passwordHashed"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                User.Role.valueOf(rs.getString("role")),
                rs.getTimestamp("lastLogin") != null ? rs.getTimestamp("lastLogin").toLocalDateTime() : null,
                rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null
        );
    }

    // Method to find all users
    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";  // Ensure your 'users' table matches these column names
        return jdbcTemplate.query(sql, userMapper);
    }

    // Method to find a user by ID
    @Override
    public User findUserById(int id) {
        String sql = "SELECT * FROM users WHERE ID = ?";
       try {
           return jdbcTemplate.queryForObject(sql, userMapper, id);
       } catch (EmptyResultDataAccessException e) {
           return null; // Return null if the user is not found
       }
    }

    @Override
    public User addUser(User user) {
        // Check if the username already exists
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, user.getUsername());
        if (count > 0) {
            throw new IllegalArgumentException("A user with this username already exists");
        }

        // Define the SQL Insert statement
        String insertQuery = "INSERT INTO users (username, passwordHashed, firstName, lastName, role, lastLogin, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Execute the insert query
        jdbcTemplate.update(insertQuery,
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().toString(),
                user.getLastLogin(),
                user.getCreatedAt()
        );

        // Retrieve the newly created user ID
        String getIdQuery = "SELECT ID FROM users WHERE username = ?";
        Integer userId = jdbcTemplate.queryForObject(getIdQuery, Integer.class, user.getUsername());
        user.setId(userId);

        // Return the newly created user
        return user;
    }

    // Method to delete a user by ID
    @Override
    public void deleteUserById(int ID) {
        String insertSql = "INSERT INTO deletedUsers (ID, username, passwordHashed, firstName, lastName, role, lastLogin, createdAt) " +
                "SELECT ID, username, passwordHashed, firstName, lastName, role, lastLogin, createdAt " +
                "FROM users WHERE ID = ?";
        jdbcTemplate.update(insertSql, ID);

        String sql = "DELETE FROM users WHERE ID = ?";
        jdbcTemplate.update(sql, ID);
    }
}
