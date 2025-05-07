package com.team14.clientProject.loggingSystem;

import com.team14.clientProject.loginPage.SecurityConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommunicationLogRepositoryImpl implements CommunicationLogRepository {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<CommunicationLog> CommunicationLogMapper;

    public CommunicationLogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createCommunicationLogRowMapper();
    }
    private void createCommunicationLogRowMapper() {
        CommunicationLogMapper = (rs, rowNum) -> {
            return new CommunicationLog(rs.getInt("logId"),
                    rs.getInt("applicantId"),
                    rs.getString("userId"),
                    rs.getString("timestamp"),
                    rs.getString("userType"),
                    rs.getString("logType"),
                    rs.getString("communicationType"),
                    rs.getString("actionTaken"),
                    rs.getString("notes"));
        };
    }
    @Override
    public List<CommunicationLog> getLogs(){
        String sql = "SELECT * FROM communicationLogs ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, CommunicationLogMapper);
    }

    @Override
    public void addEmailLog(List<String> thisApplicantId, String emailContent){
        for (String applicantId : thisApplicantId) {
            int applicantIdInt = Integer.parseInt(applicantId);
            String sql = "INSERT INTO communicationLogs (userId, applicantId, actionTaken, notes) VALUES (?,?, 'emailSent', ?)";
            jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()), applicantIdInt, emailContent);
        }
    }

    @Override
    public void addBatchEmailLog(List<String> thisApplicantId, String emailContent){
        String sql = "INSERT INTO communicationLogs (userId, applicantId, actionTaken, notes) VALUES (?,?, 'emailSent', ?)";
        int userId = getUserIdFromUsername(SecurityConfig.getCurrentUserId());
        jdbcTemplate.batchUpdate(sql, thisApplicantId, thisApplicantId.size(), (ps, applicantId) -> {
            int applicantIdInt = Integer.parseInt(applicantId);
            ps.setInt(1, userId);
            ps.setInt(2, applicantIdInt);
            ps.setString(3, emailContent);
        });
    }

    @Override
    public void addApplicantLog(){
        String sql = "INSERT INTO communicationLogs (userId, applicantId, actionTaken, notes) VALUES (?, (SELECT Max(Id) FROM applicants), 'applicantAdded', 'Applicant added to the system')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()));
    }

    @Override
    public List<CommunicationLog> getLogsByApplicantId(int applicantId){
        System.out.println(applicantId);
        String sql = "SELECT DISTINCT * FROM communicationLogs WHERE applicantId LIKE ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, CommunicationLogMapper, applicantId);
    }

    @Override
    public void editApplicantLog(int applicantId){
        String sql = "INSERT INTO communicationLogs (userId, applicantId, actionTaken, notes) VALUES (?, ?, 'applicantDetailsChanged', 'Applicant details edited')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()), applicantId);
    }
    @Override
    public void deleteApplicantLog(int applicantId){
        System.out.println(SecurityConfig.getCurrentUserId());
        String sql = "INSERT INTO communicationLogs (userId, applicantId, actionTaken, notes) VALUES (?, ?, 'applicantRemoved', 'Applicant deleted from the system')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()),applicantId);
    }
    @Override
    public List<CommunicationLog> getLogsBySession(int userId, String loginTimestamp, String logoutTimestamp){
        String sql = "SELECT * FROM communicationlogs WHERE userId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, CommunicationLogMapper, userId, loginTimestamp, logoutTimestamp);
    }
    private int getUserIdFromUsername(String username){
        String sql = "SELECT ID FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }
    @Override
    public void clearLogs(){
        String sql = "DELETE FROM communicationLogs";
        jdbcTemplate.update(sql);
    }

}
