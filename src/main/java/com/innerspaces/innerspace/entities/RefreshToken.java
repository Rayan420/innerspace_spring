package com.innerspaces.innerspace.entities;

import com.innerspaces.innerspace.entities.ApplicationUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Instant created_at;
    private Instant expires_at;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private ApplicationUser user;

}
