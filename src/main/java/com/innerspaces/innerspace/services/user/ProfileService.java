package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.ProfileImage;
import com.innerspaces.innerspace.entities.UserProfile;
import com.innerspaces.innerspace.repositories.user.ProfileImageRepository;
import com.innerspaces.innerspace.repositories.user.UserProfileRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.Objects;

@Service
@Slf4j
public class ProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final ProfileImageRepository imageRepo;

    public ProfileService(UserRepository userRepo, UserProfileRepository profileRepo, ProfileImageRepository imageRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.imageRepo = imageRepo;
    }

    @Transactional
    public ProfileImage saveProfile(String username, String bio, Date dob, MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }
            ApplicationUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user associated with the profile"));
            UserProfile userProfile = user.getUserProfile();

            ProfileImage attachment = new ProfileImage(fileName, file.getContentType(), file.getBytes());
            attachment.setProfile(userProfile);
            ProfileImage savedImage = imageRepo.save(attachment);
            user.setDateOfBirth(dob);
            user.setLastLogin();
            userProfile.setBio(bio);
            // Associate the saved image with the user profile
            userProfile.setProfileImage(savedImage);
            profileRepo.save(userProfile);
            userRepo.save(user);

            return savedImage;
        } catch (IOException e) {
            throw new Exception("Could not save File: " + fileName, e);
        }
    }

    @Transactional
    public ApplicationUser setImageUri(String profileImageUri, ProfileImage image) {
        try {
            UserProfile profile = image.getProfile();
            profile.setProfileImageUrl(profileImageUri);
            profileRepo.save(profile);
            return profile.getUser();
        } catch (Exception e) {
            throw e;
        }
    }

    public ProfileImage getAttachment(String fileId) throws Exception {
        return imageRepo
                .findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }
}
