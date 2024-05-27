package com.innerspaces.innerspace.entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FOLLOW")
@AllArgsConstructor
@Getter
@Setter
public class FollowNotification extends Notifications {
    private int followerCount;
    private int followingCount;
    private String notificationType;
    private String senderBio;

    public FollowNotification() {
        super();
    }
}
