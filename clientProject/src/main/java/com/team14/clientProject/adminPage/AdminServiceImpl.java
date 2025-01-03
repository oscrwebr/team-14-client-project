package com.team14.clientProject.adminPage;

import com.team14.clientProject.Utility.PasswordGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, @Lazy BCryptPasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        return adminRepository.findAllUsers();
    }

    @Override
    public User getUserById(int id) {
        return adminRepository.findUserById(id);
    }

    @Transactional
    @Override
    public User addUser(User user) {
        String defaultPassword = PasswordGenerator.generateDefaultPassword();
        String hashedPassword = passwordEncoder.encode(defaultPassword);
        user.setPassword(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(null);
        return adminRepository.addUser(user);
    }

    @Override
    public void deleteUser(int id) {
        adminRepository.deleteUserById(id);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        adminRepository.updateUser(user);
    }

    @Override
    public void saveResetToken(String email, String token) {
        String insertQuery = "INSERT INTO reset_tokens (email, token, expiration) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, email, token, LocalDateTime.now().plusHours(1));
    }

    @Override
    public boolean isResetTokenValid(String token) {
        String query = "SELECT COUNT(*) FROM reset_tokens WHERE token = ? AND expiration > ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, token, LocalDateTime.now());
        return count != null && count > 0;
    }

    @Override
    public String getEmailByResetToken(String token) {
        String query = "SELECT email FROM reset_tokens WHERE token = ?";
        try {
            return jdbcTemplate.queryForObject(query, String.class, token);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void invalidateResetToken(String token) {
        String deleteQuery = "DELETE FROM reset_tokens WHERE token = ?";
        jdbcTemplate.update(deleteQuery, token);
    }

    @Override
    public User getUserByEmail(String email) {
        return adminRepository.getUserByEmail(email);
    }

    @Override
    public void invalidateResetTokensByEmail(String email) {
        adminRepository.invalidateResetTokensByEmail(email);
    }
}
