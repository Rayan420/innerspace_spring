package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.CoverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverImageRepository extends JpaRepository<CoverImage, String> {
}
