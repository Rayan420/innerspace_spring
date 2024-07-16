package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.CoverImage;
import com.innerspaces.innerspace.entities.ProfileImage;
import com.innerspaces.innerspace.entities.UserProfile;
import com.innerspaces.innerspace.models.user.UpdateDTO;
import com.innerspaces.innerspace.models.user.UserDto;
import com.innerspaces.innerspace.repositories.user.CoverImageRepository;
import com.innerspaces.innerspace.repositories.user.ProfileImageRepository;
import com.innerspaces.innerspace.repositories.user.UserProfileRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.sql.Date;
import java.util.Objects;

@Service
@Slf4j
public class ProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final ProfileImageRepository imageRepo;

    private final CoverImageRepository coverImageRepository;

    public ProfileService(UserRepository userRepo, UserProfileRepository profileRepo, ProfileImageRepository imageRepo, CoverImageRepository coverImageRepository) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.imageRepo = imageRepo;
        this.coverImageRepository = coverImageRepository;
    }

    @Transactional
    public ProfileImage saveProfile(String username, String bio, Date dob, MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }
            ApplicationUser user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No user associated with the profile"));
            UserProfile userProfile = user.getUserProfile();

            ProfileImage existingImage = userProfile.getProfileImage();
            if (existingImage == null) {
                ProfileImage newImage = new ProfileImage(fileName, file.getContentType(), file.getBytes());
                newImage.setProfile(userProfile);
                userProfile.setProfileImage(newImage);
                imageRepo.save(newImage);
            } else {
                existingImage.setFileName(fileName);
                existingImage.setFileType(file.getContentType());
                existingImage.setProfile_image(file.getBytes());
                imageRepo.save(existingImage);
            }

            user.setDateOfBirth(dob);
            user.setLastLogin();
            userProfile.setBio(bio);

            profileRepo.save(userProfile);
            userRepo.save(user);

            return userProfile.getProfileImage();
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
            // if the profile exists update it

            return profile.getUser();
        } catch (Exception e) {
            throw e;
        }
    }

    public ProfileImage getProfileImageAttachment(String fileId) throws Exception {
        return imageRepo
                .findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }

    public CoverImage getCoverImageAttachment(String fileId) throws Exception {
        log.info("Retrieving cover image with ID: {}", fileId);

        return coverImageRepository
                .findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }

    public UserDto getUserProfile(long userId) {
        ApplicationUser user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("No user found with id: " + userId));
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setBio(user.getUserProfile().getBio());
        userDto.setDob(user.getDateOfBirth());
        userDto.setJoinDate(user.getDateJoined());
        userDto.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
        userDto.setPrivate(user.getUserProfile().isPrivate());
        userDto.setFollowerCount(user.getUserProfile().getFollowerCount());
        userDto.setFollowingCount(user.getUserProfile().getFollowingCount());
        userDto.setOwnedSpaceCount(user.getUserProfile().getOwnedSpaceCount());
        return userDto;
    }

    public ProfileImage updateProfile(ApplicationUser user, MultipartFile profile) {
        ProfileImage image = user.getUserProfile().getProfileImage();
        try {
            if (image == null) {
                image = new ProfileImage();
                user.getUserProfile().setProfileImage(image);
            }
            image.setProfile_image(profile.getBytes());
            image.setFileType(profile.getContentType());
            image.setFileName(user.getUsername() + "_profile_image");
            return imageRepo.save(image);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not update profile image");
        }
    }

    public CoverImage updateCover(ApplicationUser user, MultipartFile cover) {
        CoverImage image = user.getUserProfile().getCoverImage();
        try {
            if (image == null) {
                image = new CoverImage();
                user.getUserProfile().setCoverImage(image);
            }
            image.setCover_image(cover.getBytes());
            image.setFileType(cover.getContentType());
            image.setFileName(user.getUsername() + "_cover_image");
            return coverImageRepository.save(image);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not update cover image");
        }
    }

    public UpdateDTO updateUser(Long userId, String firstName, String lastName, String bio, MultipartFile pp, MultipartFile cp) {
        ApplicationUser user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("No user found with id: " + userId));
        UserProfile profile = user.getUserProfile();
        profile.setBio(bio);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (pp != null) {
            ProfileImage image = profile.getProfileImage();
            try {
                image.setProfile_image(pp.getBytes());
                image.setFileType(pp.getContentType());
                image.setFileName(user.getUsername()+"_profile_image");
                imageRepo.save(image);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not update profile image");
            }
        }

        if (cp != null) {
            CoverImage image = profile.getCoverImage();
            try {
                image.setCover_image(cp.getBytes());
                image.setFileType(cp.getContentType());
                image.setFileName(user.getUsername()+"_cover_image");
                coverImageRepository.save(image);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not update cover image");
            }
        }

        userRepo.save(user);
        profileRepo.save(profile);
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setFirstName(user.getFirstName());
        updateDTO.setLastName(user.getLastName());
        updateDTO.setBio(profile.getBio());
        updateDTO.setProfileImageUrl(profile.getProfileImageUrl());
        updateDTO.setCoverImageUrl(profile.getCoverImageUrl());
        return updateDTO;


    }
}
