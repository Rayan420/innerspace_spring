package com.innerspaces.innerspace.repositories.Timeline;

import com.innerspaces.innerspace.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository

public interface StoryAudioFileRepository extends JpaRepository<StoryAudioFile, Long> {
    List<StoryAudioFile> findByExpirationBefore(LocalDateTime now);
}
