package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.CoverImage;
import com.innerspaces.innerspace.entities.ProfileImage;
import com.innerspaces.innerspace.models.user.UpdateDTO;
import com.innerspaces.innerspace.models.user.UserDto;
import com.innerspaces.innerspace.services.user.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Date;

@RestController()
@RequestMapping("/profile")
@CrossOrigin("*")
@Slf4j
//assign doc name
@io.swagger.v3.oas.annotations.tags.Tag(name = "Profile Controller Endpoints", description = "The api endpoints responsible for user profile management.")
public class ProfileController {

    final private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }



    @RequestMapping(value = {""},
            method = RequestMethod.GET
            )
public UserDto getUserProfile(@RequestParam("user")long userId) {
        return profileService.getUserProfile(userId);
    }


    @RequestMapping(value = {"/register/{username}/", "/register/{username}"},
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationUser CompleteUserprofileSetup(@RequestParam("profile") MultipartFile profile,
                                                   @RequestParam("bio") String bio,
                                                   @RequestParam("dob") Date dob,
                                                   @PathVariable String username) throws Exception {
        log.info("Received profile register request for username: {}", username);

        ProfileImage image = profileService.saveProfile(username, bio, dob, profile);
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/profile/download/") // Add a '/' here
                .path(image.getId())
                .toUriString();
        return profileService.setImageUri(downloadUri, image);
    }

    // update user profile
    @RequestMapping(value = {"/update/{userId}", "/update/{userId}/"},
            method = RequestMethod.PUT
            ,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpdateDTO updateUserProfile(@PathVariable Long userId,
                                       @RequestParam("profile") MultipartFile profile,
                                       @RequestParam("cover") MultipartFile cover,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("bio") String bio) {
        log.info("Received profile update request for user: {}", userId);
        return profileService.updateUser(userId, firstName, lastName, bio, profile, cover);
    }

    @RequestMapping(value = {"/download/{fileId}"},
            method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> viewProfileImage(@PathVariable String fileId)
            throws Exception {
        ProfileImage image = profileService.getProfileImageAttachment(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getProfile_image()));
    }

    @RequestMapping(value = {"/cover/download/{fileId}"},
            method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> viewCoverImage(@PathVariable String fileId)
            throws Exception {
        CoverImage image = profileService.getCoverImageAttachment(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getCover_image()));
    }
}
