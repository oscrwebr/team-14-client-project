package com.team14.clientProject.loggingSystem;

import java.util.List;

public class SystemLog {
    private int systemLogId;
    private int userId;
    private String actionTaken;
    private String timestamp;
    private String notes;
    public SystemLog(int systemLogId, int userId, String actionTaken, String timestamp, String notes) {
        this.systemLogId = systemLogId;
        this.userId = userId;
        this.actionTaken = actionTaken;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public int getLogId() {
        return systemLogId;
    }

    public int getUserId() {
        return userId;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }

}
