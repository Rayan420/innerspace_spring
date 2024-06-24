package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {
    List<UserFollowing> findByUser(ApplicationUser user);

    Optional<UserFollowing> findByUserAndFollowing(ApplicationUser sender, ApplicationUser receiver);

}