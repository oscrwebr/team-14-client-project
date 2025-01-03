package com.team14.clientProject.adminPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;


@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

    @Autowired
    public AdminRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = (rs, rowNum) -> new User(
                rs.getInt("ID"),
                rs.getString("username"),
                rs.getString("passwordHashed"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getTimestamp("lastLogin") != null ? rs.getTimestamp("lastLogin").toLocalDateTime() : null,
                rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null
        );
    }

    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userMapper);
    }

    @Override
    public User findUserById(int id) {
        String sql = "SELECT * FROM users WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User addUser(User user) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, user.getUsername());
        if (count > 0) {
            throw new IllegalArgumentException("A user with this username already exists");
        }

        String insertQuery = "INSERT INTO users (username, passwordHashed, firstName, lastName, email, role, lastLogin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertQuery,
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().toString(),
                user.getLastLogin()
        );

        String getIdQuery = "SELECT ID FROM users WHERE username = ?";
        Integer userId = jdbcTemplate.queryForObject(getIdQuery, Integer.class, user.getUsername());
        user.setId(userId);

        return user;
    }

    @Override
    public void deleteUserById(int id) {
        String insertSql = "INSERT INTO deletedUsers (ID, username, passwordHashed, firstName, lastName, email, role, lastLogin, createdAt) " +
                "SELECT ID, username, passwordHashed, firstName, lastName, email, role, lastLogin, createdAt FROM users WHERE ID = ?";
        jdbcTemplate.update(insertSql, id);

        String sql = "DELETE FROM users WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public User updateUser(User user) {
        String updateQuery = "UPDATE users SET passwordHashed = ?, firstName = ?, lastName = ?, email = ?, role = ?, lastLogin = ? WHERE ID = ?";
        jdbcTemplate.update(updateQuery,
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getLastLogin(),
                user.getId()
        );
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void invalidateResetTokensByEmail(String email) {
        String deleteQuery = "DELETE FROM reset_tokens WHERE email = ?";
        jdbcTemplate.update(deleteQuery, email);
    }

    @Override
    public void saveResetToken(String email, String token) {
        String insertQuery = "INSERT INTO reset_tokens (email, token, expiration) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, email, token, LocalDateTime.now().plusHours(1));
    }
}
