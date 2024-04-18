package com.innerspaces.innerspace.entities;

import com.innerspaces.innerspace.entities.ApplicationUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow_requests")
public class FollowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private ApplicationUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private ApplicationUser receiver;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved = false;

    // Getters and setters...
}
