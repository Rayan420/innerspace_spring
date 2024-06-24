package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.UserFollower;
import com.innerspaces.innerspace.entities.UserFollowing;
import com.innerspaces.innerspace.repositories.user.UserFollowerRepository;
import com.innerspaces.innerspace.repositories.user.UserFollowingRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserFollowerRepository userFollowerRepository;

    private final UserFollowingRepository userFollowingRepository;


    @Autowired
    public FollowService(NotificationsService notificationsService, UserRepository userRepo, UserFollowerRepository userFollowerRepository, UserFollowingRepository userFollowingRepository) {
        this.notificationsService = notificationsService;
        this.userRepo = userRepo;
        this.userFollowerRepository = userFollowerRepository;
        this.userFollowingRepository = userFollowingRepository;
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

        String action;

        // Check if the sender is already following the receiver
        Optional<UserFollowing> existingFollowing = userFollowingRepository.findByUserAndFollowing(sender, receiver);
        if (existingFollowing.isPresent()) {
            // Unfollow the user
            userFollowingRepository.delete(existingFollowing.get());
            Optional<UserFollower> existingFollower = userFollowerRepository.findByUserAndFollower(receiver, sender);
            existingFollower.ifPresent(userFollowerRepository::delete);
            sender.getUserProfile().setFollowingCount(sender.getUserProfile().getFollowingCount() - 1);
            receiver.getUserProfile().setFollowerCount(receiver.getUserProfile().getFollowerCount() - 1);

            userRepo.save(sender);
            userRepo.save(receiver);

            // Log unfollow action
            log.info("User " + sender.getUsername() + " unfollowed " + receiver.getUsername());
            action = "unfollowed";
            notificationsService.createNotification(receiverId, "UNFOLLOW", senderId);

        } else {
            // Follow the user
            UserFollowing following = new UserFollowing();
            following.setUser(sender);
            following.setFollowing(receiver);
            userFollowingRepository.save(following);

            UserFollower follower = new UserFollower();
            follower.setUser(receiver);
            follower.setFollower(sender);
            userFollowerRepository.save(follower);

            sender.getUserProfile().setFollowingCount(sender.getUserProfile().getFollowingCount() + 1);
            receiver.getUserProfile().setFollowerCount(receiver.getUserProfile().getFollowerCount() + 1);

            userRepo.save(sender);
            userRepo.save(receiver);

            // Log follow action
            log.info("User " + sender.getUsername() + " followed " + receiver.getUsername());
            action = "followed";

            // Notify the receiver
            notificationsService.createNotification(receiverId, "FOLLOW", senderId);
        }
        return new ResponseEntity<>(action, HttpStatus.OK);
    }
}
