package com.innerspaces.innerspace.repositories.Timeline;

import com.innerspaces.innerspace.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
