package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.UserFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowerRepository extends JpaRepository<UserFollower, Long> {
    List<UserFollower> findByUser(ApplicationUser user);

    Optional<UserFollower> findByUserAndFollower(ApplicationUser receiver, ApplicationUser sender);

}