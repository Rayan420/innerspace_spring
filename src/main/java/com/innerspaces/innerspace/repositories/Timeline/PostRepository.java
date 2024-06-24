package com.innerspaces.innerspace.repositories.Timeline;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


    // get posts by user
    @Query("SELECT p FROM Post p WHERE p.user.userId = ?1 ORDER BY p.timestamp DESC")
    List<Post> findByUserInOrderByTimestampDesc(Long userId);

    List<Post> findByUserInOrderByTimestampDesc(Collection<ApplicationUser> users);
}