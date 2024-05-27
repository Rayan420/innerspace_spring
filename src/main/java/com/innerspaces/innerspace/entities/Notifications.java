package com.innerspaces.innerspace.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Getter
@Setter
@Entity(name = "notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;
    private Long ownerId;
    private Long senderId;
    private boolean read;
    private boolean deleted;
    private String senderImage;
    private String senderName;
    private String senderUsername;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
