package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.PostAudioFile;
import com.innerspaces.innerspace.services.user.PostService;
import com.innerspaces.innerspace.services.user.TimelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Controller
@CrossOrigin("*")
@Slf4j
@RequestMapping("/timeline")
public class TimelineController {

    private final TimelineService timelineService;
    private final PostService postService;

    @Autowired
    public TimelineController(TimelineService timelineService, PostService postService) {
        this.timelineService = timelineService;
        this.postService = postService;
    }

    @GetMapping(value={"/subscribe/{userId}"}, produces = "text/event-stream")
    public Flux<ServerSentEvent<?>> subscribe(@PathVariable Long userId) {
        return timelineService.subscribe(userId);
    }

    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestParam("userId") Long userId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("duration") int duration
                                             ) {
        timelineService.createPost(userId, file, duration);
        return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{vote}/{postId}/{senderId}")
    public ResponseEntity<String> likePost(@PathVariable String vote, @PathVariable Long postId, @PathVariable Long senderId) {
        timelineService.likePost(postId, senderId, vote);
        return new ResponseEntity<>("Post liked successfully", HttpStatus.OK);
    }


    // get user's own posts
    @GetMapping(value = {"/{userId}"})
    public ResponseEntity<?> getPosts(@PathVariable Long userId) {
        return new ResponseEntity<>(postService.getPosts(userId), HttpStatus.OK);
    }

}
