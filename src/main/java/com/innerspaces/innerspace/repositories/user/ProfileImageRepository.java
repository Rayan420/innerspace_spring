package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, String> {

}
