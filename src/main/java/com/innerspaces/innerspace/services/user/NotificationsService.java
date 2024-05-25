package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.NotificationType;
import com.innerspaces.innerspace.entities.Notifications;
import com.innerspaces.innerspace.models.user.FollowDto;
import com.innerspaces.innerspace.models.user.NotificationDto;
import com.innerspaces.innerspace.repositories.user.NotificationRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    public Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationsService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sendInitEvent(emitter, userId);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }

    private void sendInitEvent(SseEmitter emitter, Long userId) {
        try {
            List<Notifications> notifications = notificationRepository.findAllByOwnerId(userId);
            emitter.send(notifications);
        } catch (Exception e) {
            log.error("Error sending init event", e);
        }
    }

//    public Iterable<Notifications> getAllNotifications() {
//        return notificationRepository.findAll();
//    }

    public void sendNotification(NotificationDto notification, Long userId) {

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

    public Notifications createNotification(String message, Long userId, NotificationType type) {
        try  {
            Notifications notification = new Notifications();
            notification.setMessage(message);
            notification.setType(type);
            notification.setOwnerId(userId);
            Notifications savedNotification = notificationRepository.save(notification);
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setNotification(savedNotification);
            FollowDto followDto = new FollowDto();
            if (type.equals(NotificationType.FOLLOW)) {
                Optional<ApplicationUser> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    ApplicationUser applicationUser = user.get();

                    followDto.setFollowerCount(applicationUser.getFollowers().size());
                    followDto.setFollowingCount(applicationUser.getFollowing().size());
                    notificationDto.setData(followDto);

                }
            }
            sendNotification(notificationDto, userId);
            return savedNotification;
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
    }
}
