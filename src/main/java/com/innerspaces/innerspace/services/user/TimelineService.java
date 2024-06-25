package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.Post;
import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.UserFollower;
import com.innerspaces.innerspace.models.user.PostDTO;
import com.innerspaces.innerspace.repositories.user.UserFollowerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimelineService {

    private final Map<Long, Sinks.Many<ServerSentEvent<?>>> timelineEmitters = new ConcurrentHashMap<>();
    private final PostService postService;
    private final UserFollowerRepository userFollowerRepository;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    public TimelineService(PostService postService, UserFollowerRepository userFollowerRepository) {
        this.postService = postService;
        this.userFollowerRepository = userFollowerRepository;
    }

    public Flux<ServerSentEvent<?>> subscribe(Long userId) {
        Sinks.Many<ServerSentEvent<?>> sink = Sinks.many().multicast().onBackpressureBuffer();
        timelineEmitters.put(userId, sink);
        log.info("User {} subscribed to timeline updates", userId);

        // Send initial event to confirm the connection
        sink.tryEmitNext(ServerSentEvent.builder()
                .comment("Connection established")
                .build()).orThrow();

        // Send initial posts if available
        postService.getTimeline(userId)
                .collectList()
                .flatMapMany(recentPosts -> {
                    if (recentPosts.isEmpty()) {
                        return Mono.just(ServerSentEvent.builder()
                                .event("initialData")
                                .data(Collections.emptyList())
                                .build());
                    } else {
                        return Mono.just(ServerSentEvent.builder()
                                .event("initialData")
                                .data(recentPosts)
                                .build());
                    }
                })
                .doOnNext(sink::tryEmitNext)
                   .subscribe();

        return sink.asFlux().doOnTerminate(() -> unsubscribe(userId));
    }


    public void unsubscribe(Long userId) {
        timelineEmitters.remove(userId);
        log.info("User {} unsubscribed from timeline updates", userId);
    }

    private void notifyFollowers(Post post) {
        ApplicationUser user = post.getUser();
        List<UserFollower> followers = userFollowerRepository.findByUser(user);

        Set<ApplicationUser> followerUsers = followers.stream()
                .map(UserFollower::getFollower)
                .collect(Collectors.toSet());

        // Add the post creator to the list of users to notify
        followerUsers.add(user);

        PostDTO postDTO = PostDTO.fromPost(post);
        notifyUsers(postDTO, followerUsers);
    }

    private void notifyUsers(PostDTO postDTO, Set<ApplicationUser> users) {
        for (ApplicationUser user : users) {
            Sinks.Many<ServerSentEvent<?>> sink = timelineEmitters.get(user.getUserId());
            if (sink != null) {
                sink.tryEmitNext(ServerSentEvent.builder()
                        .event("newPost")
                        .data(postDTO)
                        .build()).orThrow();
            }
        }
    }

    public void createPost(Long userId, MultipartFile file, int duration) {
        try {
            Post post = postService.createPost(userId, file, duration);
            notifyFollowers(post);
        } catch (Exception e) {
            log.error("Error creating post", e);
        }
    }
}
