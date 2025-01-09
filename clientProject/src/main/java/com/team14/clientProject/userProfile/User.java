package com.team14.clientProject.userProfile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String role;

}
