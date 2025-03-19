package com.team14.clientProject.loggingSystem;

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
                    rs.getString("timestamp"));
        };
    }


    @Override
    public List<SystemLog> getLogs() {
        String sql = "select * from systemLogs order by timestamp desc";
        return jdbcTemplate.query(sql, SystemLogMapper);
    }

    @Override
    public void addUserLog() {
        String sql = "insert into systemLogs (userId, actionTaken) values ((select max(id) from users), 'addedUser')";
        jdbcTemplate.update(sql);
    }

    @Override
    public void removeUserLog(int userID) {
        String sql = "insert into systemLogs (userId, actionTaken) values (?, 'removedUser')";
        jdbcTemplate.update(sql, userID);
    }
}
