package com.team14.clientProject.loggingSystem;

import java.util.List;
public interface CommunicationLogRepository {
    List<CommunicationLog> getLogs();
    void addEmailLog(List<String> applicantId, String emailContent);
    List<CommunicationLog> getLogsByApplicantId(int applicantId);
    void addApplicantLog();
    void editApplicantLog(int applicantId);
    void deleteApplicantLog(int applicantId);
    List<CommunicationLog> getLogsBySession(int userId, String loginTimestamp, String logoutTimestamp);
}

