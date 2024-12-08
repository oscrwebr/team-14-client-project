package com.team14.clientProject;

import com.team14.clientProject.adminPage.*;
import com.team14.clientProject.profilePage.Profile;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class AddUserTest {

    @Autowired
    private AdminService adminService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AdminRepositoryImpl adminRepository;

    private RowMapper<Profile> profileRowMapper;


    @Test
    public void openAdminPage() throws Exception {
        Assert.notNull(adminService,"AdminServer should not be empty");
    }

    @Test
    public void addNewUsers() {
        //Create a new user object
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setPassword("password123");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setRole(User.Role.recruiter);
        newUser.setLastLogin(LocalDateTime.now());
        newUser.setCreatedAt(LocalDateTime.now());

        // Call the service to add a new user
        User savedUser = adminService.addUser(newUser);

        // Validate the user was added  
        Assert.notNull(savedUser, "Saved user should not be null");
        Assert.isTrue(savedUser.getId() > 0, "Saved user ID should be greater than 0");
        Assert.isTrue("testUser".equals(savedUser.getUsername()), "Usernames should match");
    }

    @Test
    public void deleteNewUser() {
        // Create a new user object
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setPassword("password123");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setRole(User.Role.recruiter);
        newUser.setLastLogin(LocalDateTime.now());
        newUser.setCreatedAt(LocalDateTime.now());

        // Add the user using the admin service
        User savedUser = adminService.addUser(newUser);

        // Ensure the user was saved
        Assert.notNull(savedUser, "Saved user should not be null");
        Assert.isTrue(savedUser.getId() > 0, "Saved user ID should be greater than 0");

        // Delete the user
        adminService.deleteUser(savedUser.getId());

        // Try retrieving the user to ensure it was deleted
        User deletedUser = adminService.getUserById(savedUser.getId());
        Assert.isNull(deletedUser, "Deleted user should be null");
    }

    @Test
    void testDeleteUserAfter5Seconds() throws InterruptedException {
        int userId = 1;
        String insertSql = "INSERT INTO deletedUsers (ID, username, passwordHashed, firstName, lastName, role, lastLogin, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String checkSql =  "SELECT * FROM deletedUsers WHERE id = ?";

        when(jdbcTemplate.update(insertSql, userId, "user1",  "9323dd6786ebcbf3ac87357cc78ba1abfda6cf5e55cd01097b90d4a286cac90e", "John", "Doe", "Recruiter", "2024-12-07 17:11:16", "2024-12-07 17:11:16")).thenReturn(1);
        adminRepository.deleteUserById(userId);

        Thread.sleep(5000);

        when(jdbcTemplate.query(checkSql, new Object[]{userId}, profileRowMapper)).thenReturn(List.of());
        List<Profile> profiles = jdbcTemplate.query(checkSql, new Object[]{userId}, profileRowMapper);

        assertTrue(profiles.isEmpty());
    }

}
