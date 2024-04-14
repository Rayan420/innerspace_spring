package com.innerspaces.innerspace.repositories.auth;

import com.innerspaces.innerspace.models.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
