package com.team14.clientProject.adminPage;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    User getUserById(int id);
    User addUser(User user);
    void deleteUser(int id);
    void updateUser(User user);
    void saveResetToken(String email, String token);
    boolean isResetTokenValid(String token);
    String getEmailByResetToken(String token);
    void invalidateResetToken(String token);
    User getUserByEmail(String email);
    void invalidateResetTokensByEmail(String email);
}
