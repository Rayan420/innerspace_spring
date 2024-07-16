package com.innerspaces.innerspace;

import com.innerspaces.innerspace.entities.*;
import com.innerspaces.innerspace.repositories.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class InnerspaceApplication {

    public static void main(String[] args) {

        ApplicationContext ctx =  SpringApplication.run(InnerspaceApplication.class, args);

    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, UserProfileRepository userProfileRepository, ProfileImageRepository imageRepo, CoverImageRepository coverImageRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role role = new Role();
            role.setAuthority("USER");
            roleRepository.save(role);

            // Sample usernames
            String[] usernames = {"john_doe", "sarah_smith", "mike_jones", "emily_brown", "alex_williams", "chris_taylor"};

            // Create 50 users
            for (int i = 0; i < 20; i++) {
                String username = usernames[i % usernames.length] + i;
                String email = "user" + i + "@example.com";
                String password = "password" + i;
                String firstName = "First" + i;
                String lastName = "Last" + i;
                Date dob = Date.valueOf(LocalDate.now().minusYears(20 + i));
                String bio = "Sample bio for user " + username;

                // Register the user
                registerDummyUser(role, username, email, password, firstName, lastName, dob, bio, userRepository, userProfileRepository, imageRepo, coverImageRepository, passwordEncoder);
            }


        };
    }

    private void registerDummyUser(Role role, String username, String email, String password, String firstName, String lastName, Date dob, String bio, UserRepository userRepository, UserProfileRepository userProfileRepository, ProfileImageRepository imageRepo, CoverImageRepository coverImageRepository, PasswordEncoder passwordEncoder) throws IOException {
        ApplicationUser user = new ApplicationUser();
        user.setUsername(username.toLowerCase());
        user.setEmail(email.toLowerCase());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dob);
        user.setPassword(passwordEncoder.encode(password));
        user.setLastLogin();

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setAuthorities(roles);

        // Save the user first
        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setBio(bio);
        profile.setLastUpdated();

        // Save the profile before setting images
        userProfileRepository.save(profile);

        // Set default profile image
        ProfileImage profileImage = new ProfileImage();
        byte[] defaultProfileImageBytes = getImageBytes("profile1.png");
        profileImage.setProfile_image(defaultProfileImageBytes);
        profileImage.setFileType("image/png");
        profileImage.setFileName(username + "_profile.png");
        profileImage.setProfile(profile);

        // Save the profile image
        imageRepo.save(profileImage);

        String profileUri = "http://localhost:8000/profile/download/" + profileImage.getId();
        profile.setProfileImageUrl(profileUri);
        profile.setProfileImage(profileImage);

        // Set default cover image
        CoverImage coverImage = new CoverImage();
        byte[] defaultCoverImageBytes = getImageBytes("cover.png");
        coverImage.setCover_image(defaultCoverImageBytes);
        coverImage.setFileType("image/png");
        coverImage.setFileName(username + "_cover.png");
        coverImage.setProfile(profile);

        // Save the cover image
        coverImageRepository.save(coverImage);

        // build the uri string using string builder
        String coverUri = "http://localhost:8000/profile/cover/download/" + coverImage.getId();

        profile.setCoverImageUrl(coverUri);
        profile.setCoverImage(coverImage);

        // Update the profile with image URLs
        userProfileRepository.save(profile);
    }

    private byte[] getImageBytes(String imageName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/" + imageName);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + imageName);
        }
        return inputStream.readAllBytes();
    }

}
