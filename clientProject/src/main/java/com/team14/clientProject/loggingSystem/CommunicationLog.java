package com.team14.clientProject.loggingSystem;
//(applicantId, userId, timestamp, userType, logType, communicationType, actionTaken, notes)
public class CommunicationLog {
    private int logId;
    private int applicantId;
    private String userId;
    private String timestamp;
    private String userType;
    private String logType;
    private String communicationType;
    private String actionTaken;
    private String notes;

    public CommunicationLog(int logId, int applicantId, String userId, String timestamp, String userType, String logType, String communicationType, String actionTaken, String notes) {
        this.logId = logId;
        this.applicantId = applicantId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.userType = userType;
        this.logType = logType;
        this.communicationType = communicationType;
        this.actionTaken = actionTaken;
        this.notes = notes;
    }

    public int getLogId() {
        return logId;
    }

    public int getApplicantId() {
        return applicantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserType() {
        return userType;
    }

    public String getLogType() {
        return logType;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public String getNotes() {
        return notes;
    }






}
