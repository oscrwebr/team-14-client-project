package com.team14.clientProject.loggingSystem;

import com.team14.clientProject.loginPage.SecurityConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemLogRepositoryImpl implements SystemLogRepository {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<SystemLog> SystemLogMapper;

    public SystemLogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createSystemLogRowMapper();
    }

    private void createSystemLogRowMapper() {
        SystemLogMapper = (rs, rowNum) -> {
            return new SystemLog(rs.getInt("systemLogId"),
                    rs.getInt("userId"),
                    rs.getString("actionTaken"),
                    rs.getString("timestamp"),
                    rs.getString("notes"));
        };
    }


    @Override
    public List<SystemLog> getLogs() {
        String sql = "SELECT * FROM systemLogs ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, SystemLogMapper);
    }

    @Override
    public void addUserLog() {
        String sql = "INSERT INTO systemLogs (userId, actionTaken) VALUES (?, 'addedUser')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()));
    }


    @Override
    public void removeUserLog(int UserID) {
        String sql = "INSERT INTO systemLogs (userId, actionTaken) VALUES (?, 'removedUser')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()));
    }
    @Override
    public void loginUser(int UserID) {
        System.out.println("Logging in user");
        String sql = "INSERT INTO systemLogs (userId, actionTaken) VALUES (?, 'login')";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()));
    }
    @Override
    public void logoutUser(int UserID) {
        String sql = "INSERT INTO systemLogs (userId, actionTaken) VALUES (?, 'logout')";
        jdbcTemplate.update(sql, UserID);

    }
    @Override
    public List<SystemLog> getWhenUserLoggedIn(int UserID, String logoutTime) {
        String sql = "SELECT * FROM systemLogs WHERE userId = ? AND timestamp < ? AND actionTaken = 'login' ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, SystemLogMapper, UserID, logoutTime);
    }
    @Override
    public List<SystemLog> getLogoutLogs() {
        String sql = "SELECT * FROM systemLogs WHERE actionTaken = 'logout' ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, SystemLogMapper);
    }
    @Override
    public List<SystemLog> getSessionLogs(int userId, String loginTime, String logoutTime) {
        String sql = "SELECT * FROM systemLogs WHERE userId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, SystemLogMapper, userId, loginTime, logoutTime);
    }
    private int getUserIdFromUsername(String username){
        String sql = "SELECT ID FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    public void clearLogs() {
        String sql = "DELETE FROM systemLogs";
        jdbcTemplate.update(sql);
    }
    public void addTraversalLog(String notes) {
        String sql = "INSERT INTO systemLogs (userId, actionTaken, notes) VALUES (?, 'traversal', ?)";
        jdbcTemplate.update(sql, getUserIdFromUsername(SecurityConfig.getCurrentUserId()), notes);
    }
}
