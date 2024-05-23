package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.services.user.ProfileService;
import com.innerspaces.innerspace.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "User Controller Endpoints",
        description = "The api endpoints responsible for user management.")

public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    @Autowired
    UserController(UserService userService, ProfileService profileService)
    {
        this.userService = userService;
        this.profileService = profileService;
    }

    // Add user endpoints here

    // search endpoint

    @RequestMapping(value = {"/search/{keyword}", "/search/{keyword}/"},
    produces = "application/json", method = RequestMethod.GET)
    public List<ApplicationUser> searchUsers(@PathVariable String keyword)
    {
        return userService.searchUsers(keyword);
    }

}
