package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.*;
import com.innerspaces.innerspace.repositories.user.NotificationRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import com.innerspaces.innerspace.utils.SseEmitterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Map<Long, Sinks.Many<Notifications>> sinks = new ConcurrentHashMap<>();

    public NotificationsService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserSubscribed(Long userId) {
        return sinks.containsKey(userId);
    }

    public SseEmitter subscribe(Long userId) {
        Sinks.Many<Notifications> sink = Sinks.many().multicast().onBackpressureBuffer();
        sinks.put(userId, sink);
        log.info("User {} subscribed to notifications", userId);

        Flux<Notifications> notificationFlux = sink.asFlux()
                .doOnSubscribe(subscription -> sendInitEvent(sink, userId));

        return SseEmitterUtils.fromFlux(notificationFlux, -1L);
    }

    public void unsubscribe(Long userId) {
        sinks.remove(userId);
        log.info("User {} unsubscribed from notifications", userId);
    }

    private void sendInitEvent(Sinks.Many<Notifications> sink, Long userId) {
        try {
            List<Notifications> recentNotifications = notificationRepository.findTop10ByOwnerIdOrderByCreatedAtDesc(userId);
            recentNotifications.forEach(sink::tryEmitNext);
        } catch (Exception e) {
            log.error("Error sending init event", e);
        }
    }

    @Async
    public void createNotification(Long userId, String type, Long senderId) {
        try {
            ApplicationUser sender = userRepository.findById(senderId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            ApplicationUser receiver = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Notifications notification = buildNotification(type, sender, receiver);

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

    private Notifications buildNotification(String type, ApplicationUser sender, ApplicationUser receiver) {
        switch (type) {
            case "FOLLOW":
                FollowNotification followNotification = new FollowNotification();
                followNotification.setMessage(sender.getUsername() + " started following you");
                followNotification.setSenderBio(sender.getUserProfile().getBio());
                followNotification.setFollowerCount(receiver.getUserProfile().getFollowerCount());
                followNotification.setNotificationType("FOLLOW");
                return followNotification;
            case "UNFOLLOW":
                UnFollowNotification unfollowNotification = new UnFollowNotification();
                unfollowNotification.setMessage(sender.getUsername() + " started following you");
                unfollowNotification.setSenderBio(sender.getUserProfile().getBio());
                unfollowNotification.setFollowerCount(receiver.getUserProfile().getFollowerCount());
                unfollowNotification.setNotificationType("UNFOLLOW");
                return unfollowNotification;
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
        Sinks.Many<Notifications> sink = sinks.get(userId);
        if (sink != null) {
            sink.tryEmitNext(notification);
        }
    }
}
