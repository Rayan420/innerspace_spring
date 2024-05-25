package com.innerspaces.innerspace.models.user;

import com.innerspaces.innerspace.entities.Notifications;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {
    Notifications notification;
    Object data;
}
