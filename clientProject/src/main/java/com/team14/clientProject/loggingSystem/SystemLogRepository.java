package com.team14.clientProject.loggingSystem;

import java.util.List;

public interface SystemLogRepository {
    List<SystemLog> getLogs();
    void addUserLog();
    void removeUserLog(int userId);
}
