package com.team14.clientProject.userProfile;

import com.team14.clientProject.addApplicant.AddApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public User getUserProfile(String username) {
        return userProfileRepository.getDataFromUsername(username);
    }


}
