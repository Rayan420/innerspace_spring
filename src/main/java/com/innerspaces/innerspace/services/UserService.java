package com.innerspaces.innerspace.services;

import com.innerspaces.innerspace.models.ApplicationUser;
import com.innerspaces.innerspace.models.Role;
import com.innerspaces.innerspace.models.UserProfile;
import com.innerspaces.innerspace.repositories.RoleRepository;
import com.innerspaces.innerspace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;


@Service
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    @Autowired
    public UserService(UserRepository userRepo, RoleRepository roleRepo)
    {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }


    public ApplicationUser registerUser(ApplicationUser user, UserProfile profile) throws Exception {
        HashSet<Role> roles = new HashSet<>();
        if(roleRepo.findByAuthority("USER").isPresent()) {
            roles.add(roleRepo.findByAuthority("USER").get());
        } else {
            throw new Exception("No 'User' Role found");
        }
        user.setAuthorities(roles);

        // Set the user for the profile
        profile.setUser(user);
        // Set the profile for the user
        user.setUserProfile(profile);

        // Save the user

        return userRepo.save(user);
    }

}
