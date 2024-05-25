package com.innerspaces.innerspace.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Getter
@Setter
@Entity(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;
    private NotificationType type;

    // add owner id
    private Long ownerId;


}
