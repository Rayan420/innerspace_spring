package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FollowService {

    private final NotificationsService notificationsService;
    private final UserRepository userRepo;

    public FollowService(NotificationsService notificationsService, UserRepository userRepo) {
        this.notificationsService = notificationsService;
        this.userRepo = userRepo;
    }

    public ResponseEntity<?> followOrUnfollowUser(Long senderId, Long receiverId) {
        // Fetch sender and receiver users
        Optional<ApplicationUser> senderOpt = userRepo.findById(senderId);
        Optional<ApplicationUser> receiverOpt = userRepo.findById(receiverId);

        // Check if sender and receiver exist
        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        ApplicationUser sender = senderOpt.get();
        ApplicationUser receiver = receiverOpt.get();

        // Check if the sender is trying to follow/unfollow themselves
        if (senderId.equals(receiverId)) {
            return new ResponseEntity<>("You cannot follow/unfollow yourself", HttpStatus.BAD_REQUEST);
        }

        // Check if the sender is already following the receiver
        if (sender.getFollowing().contains(receiver)) {
            // Unfollow the user
            sender.getFollowing().remove(receiver);
            receiver.getFollowers().remove(sender);
            sender.getUserProfile().setFollowingCount(sender.getUserProfile().getFollowingCount() - 1);
            receiver.getUserProfile().setFollowerCount(receiver.getUserProfile().getFollowerCount() - 1);
            userRepo.save(sender);
            userRepo.save(receiver);

            // Log unfollow action
            log.info("User " + sender.getUsername() + " unfollowed " + receiver.getUsername());
        } else {
            // Follow the user
            sender.getFollowing().add(receiver);
            receiver.getFollowers().add(sender);
            sender.getUserProfile().setFollowingCount(sender.getUserProfile().getFollowingCount() + 1);
            receiver.getUserProfile().setFollowerCount(receiver.getUserProfile().getFollowerCount() + 1);

            userRepo.save(sender);
            userRepo.save(receiver);

            // Create and send follow notification
            notificationsService.createNotification(receiverId, "FOLLOW", senderId);

            // Log follow action
            log.info("User " + sender.getUsername() + " followed " + receiver.getUsername());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
