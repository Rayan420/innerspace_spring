package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.*;
import com.innerspaces.innerspace.repositories.user.NotificationRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Map<Long, Sinks.Many<ServerSentEvent<?>>> notificationEmitters = new ConcurrentHashMap<>();

    public NotificationsService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserSubscribed(Long userId) {
        return notificationEmitters.containsKey(userId);
    }

    public Flux<ServerSentEvent<?>> subscribe(Long userId) {
        Sinks.Many<ServerSentEvent<?>> sink = Sinks.many().multicast().onBackpressureBuffer();
        notificationEmitters.put(userId, sink);
        log.info("User {} subscribed to notifications", userId);
// Send initial event to confirm the connection
        sink.tryEmitNext(ServerSentEvent.builder()
                .comment("Connection established")
                .build()).orThrow();

        // Send initial notifications if available
        getNotifications(userId)
                .collectList()
                .flatMapMany(notifications -> {
                    if (notifications.isEmpty()) {
                        return Flux.just(ServerSentEvent.builder()
                                .event("initialData")
                                .data(Collections.emptyList())
                                .build());
                    } else {
                        return Flux.just(ServerSentEvent.builder()
                                .event("initialData")
                                .data(notifications)
                                .build());
                    }
                })
                .subscribe(sink::tryEmitNext);


        return sink.asFlux().doOnTerminate(() -> unsubscribe(userId));

    }

    public void unsubscribe(Long userId) {
        notificationEmitters.remove(userId);
        log.info("User {} unsubscribed from notifications", userId);
    }

    Flux<Notifications> getNotifications(Long userId) {
        return Flux.defer(() -> {
            List<Notifications> notifications = notificationRepository.findTop10ByOwnerIdOrderByCreatedAtDesc(userId);
            return Flux.fromIterable(notifications);
        });
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
        Sinks.Many<ServerSentEvent<?>> sink = notificationEmitters.get(userId);
        if (sink != null) {
            sink.tryEmitNext(ServerSentEvent.builder()
                    .event("newPost")
                    .data(notification)
                    .build()).orThrow();
        }
    }

}
