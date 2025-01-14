package com.team14.clientProject.adminPage;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    User getUserById(int id);
    User addUser(User user);
    void deleteUser(int id);
    String validatePassword(String password);
}
