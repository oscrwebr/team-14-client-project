package com.team14.clientProject.adminPage;

import java.util.List;

public interface AdminRepository {
    List<User> findAllUsers();
    User findUserById(int id);
    User addUser(User user);
    void deleteUserById(int id);
    String validatePassword(String password);
    User findByUsername(String username);
}
