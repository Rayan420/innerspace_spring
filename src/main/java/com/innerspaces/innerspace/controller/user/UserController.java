package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.services.user.FollowService;
import com.innerspaces.innerspace.services.user.ProfileService;
import com.innerspaces.innerspace.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final FollowService followService;
    @Autowired
    UserController(UserService userService, ProfileService profileService, FollowService followService)
    {
        this.userService = userService;
        this.profileService = profileService;
        this.followService = followService;
    }

    // Add user endpoints here

    // search endpoint

    @RequestMapping(value = {"/search/{keyword}", "/search/{keyword}/"},
    produces = "application/json", method = RequestMethod.GET)
    public List<ApplicationUser> searchUsers(@PathVariable String keyword)
    {
        return userService.searchUsers(keyword);
    }

    // follow user
    @RequestMapping(value = {"/follow/{senderId}/{receiverId}", "/follow/{senderId}/{receiverId}/"}
    ,method = RequestMethod.POST)
    public ResponseEntity<?> followUser(@PathVariable Long senderId, @PathVariable Long receiverId)
    {

        return followService.followOrUnfollowUser(senderId, receiverId);
    }


}
