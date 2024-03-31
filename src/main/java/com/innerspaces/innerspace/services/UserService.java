package com.innerspaces.innerspace.services;
import com.innerspaces.innerspace.controller.AuthenticationController;
import com.innerspaces.innerspace.models.user.ApplicationUser;
import com.innerspaces.innerspace.models.user.RegistrationObject;
import com.innerspaces.innerspace.models.user.Role;
import com.innerspaces.innerspace.models.user.UserProfile;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;


@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    @Autowired
    public UserService(UserRepository userRepo, RoleRepository roleRepo)
    {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }


    public ApplicationUser registerUser(RegistrationObject ro) throws Exception {
        ApplicationUser user = new ApplicationUser();
        HashSet<Role> roles = new HashSet<>();
        if(roleRepo.findByAuthority("USER").isPresent()) {
            roles.add(roleRepo.findByAuthority("USER").get());
        } else {
            throw new Exception("No 'User' Role found");
        }
        user.setAuthorities(roles);

        // Set the user for the profile
        logger.info("Received registration request for user: {}", ro);
        UserProfile profile = new UserProfile();
        user.setUserProfile(profile);
        user.getUserProfile().setBio("this is a test bio set fromm the code");
        logger.info("user profile created: {}", user.getUserProfile()); // This will log the profile data

        user.setEmail(ro.getEmail());
        user.setFirstName(ro.getFirstName());
        user.setLastName(ro.getLastName());
        user.setDateOfBirth(ro.getDob());
        user.setDateJoined();

        return userRepo.save(user);
    }

}
