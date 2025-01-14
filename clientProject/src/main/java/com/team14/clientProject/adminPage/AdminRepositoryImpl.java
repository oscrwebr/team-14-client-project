package com.team14.clientProject.adminPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                rs.getString("role"),
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
        try {
            user.setPassword(validatePassword(user.getPassword()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
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
    @Override
    public String validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (isCommonPassword(password)) {
            throw new IllegalArgumentException("Password is too common");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        password = encoder.encode(password);
        return password;
    }
    private boolean isCommonPassword(String password) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/commonPasswords.txt")) {
            if (inputStream == null) {
                throw new RuntimeException("Common passwords file not found.");
            }
            List<String> commonPasswords = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList());
            return commonPasswords.contains(password.toLowerCase());
        } catch (IOException e) {
            throw new RuntimeException("Error reading common passwords file", e);
        }
    }


}
