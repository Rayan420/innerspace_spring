package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.models.user.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByUsername(String username);

    @Query("select u.username from ApplicationUser u where u.username like ?1%")
    Optional<String> findByUsernameLike(String username);
    Optional<ApplicationUser> findByEmail(String username);
}
