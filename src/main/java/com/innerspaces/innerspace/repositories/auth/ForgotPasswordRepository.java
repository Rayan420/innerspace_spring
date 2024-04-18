package com.innerspaces.innerspace.repositories.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    @Query("select f from ForgotPassword f where f.otp=?1 and f.user=?2")
    Optional<ForgotPassword> findByOtpAndUser(String otp, ApplicationUser user);
}
