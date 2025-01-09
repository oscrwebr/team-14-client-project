package com.team14.clientProject.userProfile;

import com.team14.clientProject.addApplicant.AddApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface UserProfileService {

    public User getUserProfile(String username);

}
