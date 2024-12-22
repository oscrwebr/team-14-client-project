package com.team14.clientProject.profilePage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

class ProfilePageRepositoryImplTest {

    private ProfilePageRepositoryImpl profilePageRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        try{
        MockitoAnnotations.openMocks(this);
        profilePageRepository = new ProfilePageRepositoryImpl(jdbcTemplate);
        }catch(Exception e){
            System.out.println(e);
        }

    }

    // Test for getting profiles from the database
    @Test
    void testGetProfiles() {

        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";
        List<Profile> mockProfiles = List.of(new Profile(1, "John", "Doe", "New York", "john.doe@example.com",
                "1234567890", "Tech Conference", "Java Developer"));
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenReturn(mockProfiles);

        List<Profile> profiles = profilePageRepository.getProfiles();

        assertEquals(1, profiles.size());
        assertEquals("John", profiles.get(0).getFirstName());
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }


    // Test for getting a profile by ID from the database
    @Test
    void testGetProfileById() {

        int testId = 1;
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id " +
                "WHERE a.id = ?";
        Profile mockProfile = new Profile(1, "John", "Doe", "New York", "john.doe@example.com",
                "1234567890", "Tech Conference", "Java Developer");
        when(jdbcTemplate.queryForObject(eq(sql), any(RowMapper.class), eq(testId))).thenReturn(mockProfile);

        Profile profile = profilePageRepository.getProfileById(testId);

        assertNotNull(profile);
        assertEquals("John", profile.getFirstName());
        verify(jdbcTemplate, times(1)).queryForObject(eq(sql), any(RowMapper.class), eq(testId));
    }


    // Test for getting profiles from the database when the database returns null fields
    @Test
    void testGetProfiles_NullFields() {

        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";
        Profile mockProfile = new Profile(1, null, "Smith", null, "smith@example.com",
                "9876543210", "Conference", null);
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenReturn(List.of(mockProfile));

        List<Profile> profiles = profilePageRepository.getProfiles();

        assertEquals(1, profiles.size());
        assertNull(profiles.get(0).getFirstName());
        assertEquals("Smith", profiles.get(0).getLastName());
        assertEquals("smith@example.com", profiles.get(0).getEmail());
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }



    // Test for getting profiles from the database when the database returns an empty result
    @Test
    void testGetProfiles_EmptyResult() {

        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenReturn(List.of());

        List<Profile> profiles = profilePageRepository.getProfiles();

        assertTrue(profiles.isEmpty()); // Ensure the returned list is empty
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }


    // Test for getting a profile by ID from the database when the database returns null fields
    @Test
    void testGetProfiles_NullPreferencesAndJobDetails() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";

        // Profile with null preferences and job details
        Profile mockProfile = new Profile(1, "Jane", "Doe", "London", "jane.doe@example.com",
                "123456789", "Tech Conference", "Java Developer");
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenReturn(List.of(mockProfile));

        List<Profile> profiles = profilePageRepository.getProfiles();

        assertEquals(1, profiles.size());
        assertNull(profiles.get(0).getPreferences());
        assertNull(profiles.get(0).getJobDetails());
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }


    // Test for getting a profile by ID from the database there is an error in the database
    @Test
    void testGetProfiles_DatabaseError() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";

        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> profilePageRepository.getProfiles());

        assertEquals("Database error", exception.getMessage());
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }




    @Test
    void testGetProfileById_ProfileNotFound() {
        int testId = 999; // Assume this ID doesn't exist in the database
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id " +
                "WHERE a.id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), any(RowMapper.class), eq(testId))).thenReturn(null);

        Profile profile = profilePageRepository.getProfileById(testId);

        assertNull(profile); // Ensure no profile is returned
        verify(jdbcTemplate, times(1)).queryForObject(eq(sql), any(RowMapper.class), eq(testId));
    }

    @Test
    void testGetProfiles_MultipleProfiles() {
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id";

        Profile mockProfile1 = new Profile(1, "John", "Doe", "New York", "john.doe@example.com", "1234567890", "Tech Conference", "Java Developer");
        Profile mockProfile2 = new Profile(2, "Jane", "Smith", "San Francisco", "jane.smith@example.com", "9876543210", "Web Development", "Python Developer");
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class))).thenReturn(List.of(mockProfile1, mockProfile2));

        List<Profile> profiles = profilePageRepository.getProfiles();

        assertEquals(2, profiles.size()); // Ensure two profiles are returned
        assertEquals("John", profiles.get(0).getFirstName());
        assertEquals("Jane", profiles.get(1).getFirstName());
        verify(jdbcTemplate, times(1)).query(eq(sql), any(RowMapper.class));
    }


    @Test
    void testGetProfileById_MultipleProfilesWithSameId() {
        int testId = 1;
        String sql = "SELECT a.*, p.SubscribeToNewsLetter, p.SubscribeToBulletins, p.SubscribeToJobUpdates," +
                "d.currentPosition, d.status " +
                "FROM applicants a " +
                "LEFT JOIN applicantpreferences p ON a.Id = p.Id " +
                "LEFT JOIN applicationdetails d ON a.Id = d.Id " +
                "WHERE a.id = ?";

        Profile mockProfile1 = new Profile(1, "John", "Doe", "New York", "john.doe@example.com", "1234567890", "Tech Conference", "Java Developer");
        Profile mockProfile2 = new Profile(1, "John", "Doe", "San Francisco", "john.doe2@example.com", "9876543210", "Web Conference", "JavaScript Developer");
        when(jdbcTemplate.queryForObject(eq(sql), any(RowMapper.class), eq(testId))).thenReturn(mockProfile1); // Mocking for one result

        Profile profile = profilePageRepository.getProfileById(testId);

        assertNotNull(profile);
        assertEquals("John", profile.getFirstName()); // Ensure the correct profile is returned
        verify(jdbcTemplate, times(1)).queryForObject(eq(sql), any(RowMapper.class), eq(testId));
    }


}
