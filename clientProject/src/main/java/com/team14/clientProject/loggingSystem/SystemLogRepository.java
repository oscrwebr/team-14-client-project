package com.team14.clientProject.loggingSystem;

import java.util.List;

public interface SystemLogRepository {
    List<SystemLog> getLogs();
    void addUserLog();
    void removeUserLog(int UserId);
    void loginUser(int UserId);
    void logoutUser(int UserId);
    List getWhenUserLoggedIn(int UserId, String logoutTime);
    List<SystemLog> getLogoutLogs();
    List<SystemLog> getSessionLogs(int userId, String loginTime, String logoutTime);
}
