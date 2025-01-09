package com.team14.clientProject.userProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository {

    public User getDataFromUsername(String username);
}
