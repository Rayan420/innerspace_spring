package com.innerspaces.innerspace.services;

import com.innerspaces.innerspace.controller.AuthenticationController;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.models.user.ApplicationUser;
import com.innerspaces.innerspace.models.user.LoginResponseDTO;
import com.innerspaces.innerspace.models.user.RegistrationObject;
import com.innerspaces.innerspace.models.user.Role;
import com.innerspaces.innerspace.models.user.UserProfile;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import com.innerspaces.innerspace.utils.UsernameRecomAlgo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;



    public ApplicationUser registerUser(RegistrationObject ro) throws Exception {
        ApplicationUser user = new ApplicationUser();
        HashSet<Role> roles = new HashSet<>();

        roles.add(roleRepo.findByAuthority("USER").orElseThrow(RoleDoesNotExistException::new));
        user.setAuthorities(roles);
        logger.info("Received registration request for user password: {}", ro.getPassword());

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
            UsernameRecomAlgo recomAlgo = new UsernameRecomAlgo(userRepo);
            names = recomAlgo.usernameRecommendation(ro.getUsername());
            throw new UsernameOrEmailAlreadyTaken(names, ro.getUsername() );

        }
        user.setUsername(ro.getUsername());
        String encryPass = passwordEncoder.encode(ro.getPassword());
        user.setPassword(encryPass);
        logger.info("Received registration request for user encrypted password: {}",encryPass);

        return userRepo.save(user);
    }




    @Autowired
    public AuthenticationService( TokenService tokenService, RoleRepository roleRepo, PasswordEncoder passwordEncoder, UserRepository userRepo)
    {
        this.tokenService = tokenService;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    public LoginResponseDTO loginUser(String username, String password){
        logger.info("Received credentials: Username: {}, Password: {}", username, password);

        try{
            // Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Check if authentication was successful
            if (auth.isAuthenticated()) {
                logger.info("User {} authenticated successfully", username);

                // Generate JWT token
                String token = tokenService.generateJwt(auth);

                // Retrieve user from repository
                Optional<ApplicationUser> optionalUser = userRepo.findByUsername(username);
                if (optionalUser.isPresent()) {
                    ApplicationUser user = optionalUser.get();

                    // Log the user details
                    logger.info("User details retrieved: {}", user);

                    // Return LoginResponseDTO with user and token
                    return new LoginResponseDTO(user, token, "");
                } else {
                    logger.error("User {} not found in the repository", username);
                    // Handle case where user is not found
                    return new LoginResponseDTO(null, null, null);
                }
            } else {
                // Handle case where authentication failed
                logger.error("Authentication failed for user {}", username);
                return new LoginResponseDTO(null, null, null);
            }
        } catch(AuthenticationException e){
            // Handle authentication exception
            logger.error("Authentication exception for user {}: {}", username, e.getMessage());
            return new LoginResponseDTO(null, null, null);
        }
    }

}
