package com.innerspaces.innerspace.models.user;

import com.innerspaces.innerspace.entities.Notifications;

import java.sql.Date;
import java.time.Instant;

public class NotificationResponse {
    private Notifications notification;
    private String type;

    private final Instant date =  Instant.now();

    public NotificationResponse(Notifications notification, String type) {
        this.notification = notification;
        this.type = type;
    }

    public Notifications getNotification() {
        return notification;
    }

    public void setNotification(Notifications notification) {
        this.notification = notification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}