package com.innerspaces.innerspace.entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("LIKE")
@Getter
@Setter
public class LikeNotification extends Notifications {

  // add post data later
    private String notificationType;
    public LikeNotification() {
        super();
    }
}
