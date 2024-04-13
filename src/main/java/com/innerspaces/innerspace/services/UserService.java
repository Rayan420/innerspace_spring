package com.innerspaces.innerspace.services;
import com.innerspaces.innerspace.controller.AuthenticationController;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

        roles.add(roleRepo.findByAuthority("USER").orElseThrow(RoleDoesNotExistException::new));
        user.setAuthorities(roles);

        // Set the user for the profile
        logger.info("Received registration request for user: {}", ro);
        UserProfile profile = new UserProfile();
        profile.setBio(ro.getBio());
        profile.setProfilePictureUrl(ro.getProfilePictureUrl());
        user.setUserProfile(profile);
        logger.info("user profile created: {}", user.getUserProfile()); // This will log the profile data

        if(userRepo.findByEmail(ro.getEmail()).isPresent())
        {
            throw new UsernameOrEmailAlreadyTaken(ro.getEmail() );
        }
        user.setEmail(ro.getEmail());
        user.setFirstName(ro.getFirstName());
        user.setLastName(ro.getLastName());
        user.setDateOfBirth(ro.getDob());
        if(userRepo.findByUsername(ro.getUsername()).isPresent())
        {
            List<String> names = new ArrayList<>();
            names = usernameRecommendation(ro.getUsername());
            throw new UsernameOrEmailAlreadyTaken(names, ro.getUsername() );

        }
        user.setUsername(ro.getUsername());

        return userRepo.save(user);
    }


    public  List<String> usernameRecommendation(String username)
    {
        Set<String> usernames = new HashSet<>();
        usernames.add(userRepo.findByUsernameLike(username).orElse("NO RECOMMENDED USERNAME"));
        List<String> suggestionList = new ArrayList<>();
        while(suggestionList.size() !=4)
        {
            long randomNum = (long) Math.floor(Math.random() * 1_00);
            String suggestion;
            suggestion =  username + randomNum;
            if(!username.contains(suggestion) && !suggestionList.contains(suggestion)){
                suggestionList.add(suggestion);
            }
        }

        return suggestionList;
    }

}
