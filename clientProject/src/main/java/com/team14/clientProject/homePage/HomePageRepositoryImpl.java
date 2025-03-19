package com.team14.clientProject.homePage;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class HomePageRepositoryImpl implements HomePageRepository {
    private JdbcTemplate jdbc;

    private RowMapper<Applicants> ApplicantRowMapper;

    public HomePageRepositoryImpl(JdbcTemplate aJdbc) {
        this.jdbc = aJdbc;
        setHomePageMapper();
    }

    // Row mapper for the applicant object
    private void setHomePageMapper() {
        ApplicantRowMapper = (rs, rowNum) -> {
            Applicants applicant = new Applicants(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("location"),
                    rs.getString("email"),
                    rs.getString("phoneNumber"),
                    rs.getString("eventAttended"),
                    rs.getString("skill"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt")
            );
            return applicant;
        };
    }

    // Method to get the 10 most recent profiles in the database
    @Override
    public List<Applicants> get10MostRecentProfiles() {
        String sql =
                "select * " +
                "from applicants " +
                "order by createdAt " +
                "desc limit 10";
        return jdbc.query(sql, ApplicantRowMapper);
    }

    @Override
    public Applicants getApplicantById(int id) {
        String sql =
                "select * " +
                "from applicants " +
                "where id = ?";
        return jdbc.queryForObject(sql, ApplicantRowMapper, id);
    }


}
