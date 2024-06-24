package com.innerspaces.innerspace.models.user;

import com.innerspaces.innerspace.entities.Notifications;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class NotificationResponse {
    private Notifications notification;
    private String type;

    private final Instant date =  Instant.now();

    public NotificationResponse(Notifications notification, String type) {
        this.notification = notification;
        this.type = type;
    }

// from notification
    public static NotificationResponse fromNotification(Notifications notification, String type) {
        return new NotificationResponse(notification, type);
    }

}