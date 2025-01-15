package com.team14.clientProject.adminPage;

import com.team14.clientProject.loggingSystem.CommunicationLog;
import com.team14.clientProject.loggingSystem.CommunicationLogRepositoryImpl;
import com.team14.clientProject.loggingSystem.SystemLog;
import com.team14.clientProject.loggingSystem.SystemLogRepositoryImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private List<CommunicationLog> communicationLogs;
    private List<SystemLog> systemLogs;
    private List<Object> combinedLogs;
    @Autowired
    private CommunicationLogRepositoryImpl communicationLogRepository;

    @Autowired
    private SystemLogRepositoryImpl systemLogRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Autowired
    public void init() {
        communicationLogs = communicationLogRepository.getLogs();
        systemLogs = systemLogRepository.getLogs();
        combineLogs();
    }



    @Override
    public List<Object> getSessionLogs(int userId, String logoutTime) {
        List<Object> sessionLogs = new ArrayList<>();
        String loginTime = systemLogRepository.getWhenUserLoggedIn(userId, logoutTime).get(0).getTimestamp();
        System.out.println("Login time: " + loginTime);
        sessionLogs.addAll(communicationLogRepository.getLogsBySession(userId, loginTime, logoutTime));
        sessionLogs.addAll(systemLogRepository.getSessionLogs(userId, loginTime, logoutTime));
        System.out.println(sessionLogs);
        return sortLogs(sessionLogs);
    }

    @Override
    public List<Object> getCombinedLogs() {
        combineLogs();
        return combinedLogs;
    }

    private List<Object> sortLogs(List<Object> logList){
        logList.sort((o1, o2) -> {
            Timestamp t1 = o1 instanceof CommunicationLog ? Timestamp.valueOf(((CommunicationLog) o1).getTimestamp()) : Timestamp.valueOf(((SystemLog) o1).getTimestamp());
            Timestamp t2 = o2 instanceof CommunicationLog ? Timestamp.valueOf(((CommunicationLog) o2).getTimestamp()) : Timestamp.valueOf(((SystemLog) o2).getTimestamp());
            return t2.compareTo(t1);
        });
        System.out.println(logList);
        return logList;
    }


    private void combineLogs() {
        combinedLogs = new ArrayList<>();
        combinedLogs.addAll(communicationLogRepository.getLogs());
        combinedLogs.addAll(systemLogRepository.getLogs());
        sortLogs(combinedLogs);
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
