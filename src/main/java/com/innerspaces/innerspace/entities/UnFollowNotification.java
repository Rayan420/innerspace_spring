package com.innerspaces.innerspace.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("UNFOLLOW")
@AllArgsConstructor
@Getter
@Setter
public class UnFollowNotification extends Notifications {
    private int followerCount;
    private String notificationType;
    private String senderBio;

    public UnFollowNotification() {
        super();
    }
}
