package com.innerspaces.innerspace.repositories.Timeline;

import com.innerspaces.innerspace.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostAudioFileRepository extends JpaRepository<PostAudioFile, Long> {
    Optional<PostAudioFile> findById(String id);
}


