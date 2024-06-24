package com.innerspaces.innerspace.repositories.Timeline;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUserInAndExpirationAfterOrderByTimestampDesc(Set<ApplicationUser> users, LocalDateTime now);
}