package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByEmail(String email);


    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findByRefreshId(String refreshId);
    @Query("select u.username from ApplicationUser u where u.username like ?1%")
    Optional<String> findByUsernameLike(String username);
}
