package com.team14.clientProject.adminPage;

import java.util.List;

public interface AdminRepository {
    List<User> findAllUsers();
    User findUserById(int id);
    User addUser(User user);
    void deleteUserById(int id);
    User updateUser(User user);
    User getUserByEmail(String email);
    void invalidateResetTokensByEmail(String email);
    void saveResetToken(String email, String token);
}
