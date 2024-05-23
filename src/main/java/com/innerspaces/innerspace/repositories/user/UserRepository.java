package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByEmail(String email);


    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findByRefreshId(String refreshId);
    @Query("select u.username from ApplicationUser u where u.username like ?1%")
    Optional<String> findByUsernameLike(String username);

    // find all users with a username firstname or lastname that is like the given string
    @Query("SELECT u FROM ApplicationUser u WHERE u.username LIKE ?1 OR u.firstName LIKE ?1 OR u.lastName LIKE ?1")
    List<ApplicationUser> searchUsers(String keyword);
}
