package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.*;
import com.innerspaces.innerspace.repositories.user.NotificationRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public boolean isUserSubscribed(Long userId) {
        return emitters.containsKey(userId);
    }

    public NotificationsService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(TimeUnit.HOURS.toMillis(1));
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("User {} unsubscribed from notifications", userId);
            log.info("size of emitters: " + emitters.size());

        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.info("User {} timed out", userId);
            log.info("size of emitters: " + emitters.size());

        });
        emitter.onError((e) -> {
            emitters.remove(userId);
            log.error("Error occurred for user {}", userId, e);
            log.info("size of emitters: " + emitters.size());

        });
        emitters.put(userId, emitter);
        log.info("User {} subscribed to notifications", userId);
        System.out.println("User {} subscribed to notifications" + userId);
        log.info("size of emitters: " + emitters.size());
        sendInitEvent(emitter, userId);

        return emitter;
    }

    // unsubscribe method
    public void unsubscribe(Long userId) {
        emitters.remove(userId);
        log.info("User {} unsubscribed from notifications", userId);
        log.info("size of emitters: " + emitters.size());
    }

    private void sendInitEvent(SseEmitter emitter, Long userId) {
        try {
            List<Notifications> recentNotifications = notificationRepository.findTop10ByOwnerIdOrderByCreatedAtDesc(userId);
            emitter.send(recentNotifications);
        } catch (Exception e) {
            log.error("Error sending init event", e);
        }
    }

    @Async
    public void createNotification(Long userId, String type, Long senderId) {
        try {
            ApplicationUser sender = userRepository.findById(senderId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Notifications notification = buildNotification(type, sender);

            notification.setOwnerId(userId);
            notification.setSenderName(sender.getFirstName() + " " + sender.getLastName());
            notification.setSenderId(senderId);
            notification.setSenderImage(sender.getUserProfile().getProfileImageUrl());
            notification.setSenderUsername(sender.getUsername());

            Notifications savedNotification = notificationRepository.save(notification);
            sendNotification(savedNotification, userId);
        } catch (Exception e) {
            log.error("Error creating notification", e);
        }
    }

    private Notifications buildNotification(String type, ApplicationUser sender) {
        switch (type) {
            case "FOLLOW":
                FollowNotification followNotification = new FollowNotification();
                followNotification.setMessage(sender.getUsername() + " started following you");
                followNotification.setSenderBio(sender.getUserProfile().getBio());
                followNotification.setFollowerCount(sender.getUserProfile().getFollowerCount());
                followNotification.setFollowingCount(sender.getUserProfile().getFollowingCount());
                followNotification.setNotificationType("FOLLOW");
                return followNotification;
            case "LIKE":
                LikeNotification likeNotification = new LikeNotification();
                likeNotification.setMessage(sender.getUsername() + " liked your post");
                likeNotification.setNotificationType("LIKE");
                return likeNotification;
            default:
                throw new IllegalArgumentException("Invalid notification type");
        }
    }

    @Async
    public void sendNotification(Notifications notification, Long userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(notification);
            } catch (Exception e) {
                emitters.remove(userId);
                log.error("Error sending notification", e);
            }
        }
    }
}
