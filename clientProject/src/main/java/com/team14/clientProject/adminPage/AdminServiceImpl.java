package com.team14.clientProject.adminPage;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
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
    public User addUser(User user) {
        return adminRepository.addUser(user);
    }

    @Override
    public void deleteUser(int id) {
        adminRepository.deleteUserById(id);
    }

    @Override
    public String validatePassword(String password) {
        return adminRepository.validatePassword(password);
    }
}
