package com.team14.clientProject.Utility;

import java.util.UUID;

public class PasswordGenerator {

    public static String generateDefaultPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
