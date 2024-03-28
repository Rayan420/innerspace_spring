package com.innerspaces.innerspace.controller;

import com.innerspaces.innerspace.models.ApplicationUser;
import com.innerspaces.innerspace.models.UserProfile;
import com.innerspaces.innerspace.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody ApplicationUser user) throws Exception {
        // Extract the profile data from the user object
        UserProfile profile = user.getUserProfile();

        logger.info("Received registration request for user: {}", user);
        logger.info("Received profile data: {}", profile); // This will log the profile data

        // Register the user with the extracted profile
        return userService.registerUser(user, profile);
    }


}
