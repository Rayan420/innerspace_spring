package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.ProfileImage;
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
public class ProfileController {

    final private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @RequestMapping(value = {"/register/{username}/", "/register/{username}"},
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationUser CompleteUserprofileSetup(@RequestParam("profile") MultipartFile profile,
                                                   @RequestParam("bio") String bio,
                                                   @RequestParam("dob") Date dob,
                                                   @PathVariable String username) throws Exception {
        log.info("Received profile register request for username: {}", username);

        try {
            ProfileImage image = profileService.saveProfile(username, bio, dob, profile);
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/profile/download/") // Add a '/' here
                    .path(image.getId())
                    .toUriString();
            return profileService.setImageUri(downloadUri, image);
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = {"/download/{fileId}"},
            method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> viewProfileImage(@PathVariable String fileId)
            throws Exception {
        try {
            ProfileImage image = profileService.getAttachment(fileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                    .body(new ByteArrayResource(image.getProfile_image()));
        } catch (Exception e) {
            throw e;
        }
    }
}