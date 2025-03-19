package com.team14.clientProject.loggingSystem;

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
                    rs.getInt("userId"),
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
        String sql = "select * from communicationlogs order by timestamp desc";
        return jdbcTemplate.query(sql, CommunicationLogMapper);
    }

    @Override
    public void addEmailLog(List<String> thisApplicantId, String emailContent){
        for (String applicantId : thisApplicantId) {
            int applicantIdInt = Integer.parseInt(applicantId);
            String sql = "insert into communicationlogs (applicantId, actionTaken, notes) values (?, 'emailSent', ?)";
            jdbcTemplate.update(sql, applicantIdInt, emailContent);
        }
    }
    @Override
    public void addApplicantLog(){
        String sql = "insert into communicationLogs (applicantId, actionTaken, notes) values ((select max(id) from applicants), 'applicantAdded', 'Applicant added to the system')";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<CommunicationLog> getLogsByApplicantId(int applicantId){
        String sql = "select distinct * from communicationlogs where applicantId like ? order by timestamp desc";
        return jdbcTemplate.query(sql, CommunicationLogMapper, applicantId);
    }

    @Override
    public void editApplicantLog(int applicantId){
        String sql = "insert into communicationLogs (applicantId, actionTaken, notes) values (?, 'applicantDetailsChanged', 'Applicant details edited')";
        jdbcTemplate.update(sql, applicantId);
    }
    @Override
    public void deleteApplicantLog(int applicantId){
        String sql = "insert into communicationLogs (applicantId, actionTaken, notes) values (?, 'applicantRemoved', 'Applicant deleted from the system')";
        jdbcTemplate.update(sql, applicantId);
    }

}
