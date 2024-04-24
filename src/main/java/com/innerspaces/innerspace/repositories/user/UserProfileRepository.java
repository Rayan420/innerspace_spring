package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    //find profile by username
    Optional<UserProfile> findUserProfileByUser(ApplicationUser user);
}
