package com.team14.clientProject.adminPage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,}$", message = "Password must be at least 8 characters long and alphanumeric")
    private String password;

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Role cannot be empty")
    private String role;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

}