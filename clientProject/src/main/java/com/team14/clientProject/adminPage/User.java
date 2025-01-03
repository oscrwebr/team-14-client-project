package com.team14.clientProject.adminPage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    private String password;

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&*+/=?`{}~^.-]+@[a-zA-Z0-9.-]+$", message="Email must be in a valid format")
    private String email;

    @NotEmpty(message = "Role cannot be empty")
    private String role;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public User(int i, String email, String mohammed, String bolaji, String s, String roleUser, Object o, Object object) {
        this.email = email;
    }
}