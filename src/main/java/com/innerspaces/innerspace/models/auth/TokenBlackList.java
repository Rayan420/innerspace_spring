package com.innerspaces.innerspace.models.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "token_blacklist_blacklistedtoken")
public class TokenBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant blacklisted_at;

    @OneToOne
    @JoinColumn(name = "token_id", referencedColumnName = "id")
    private RefreshToken refreshToken;

}
