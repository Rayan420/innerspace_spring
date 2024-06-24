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

    @GetMapping(value={"/subscribe"}, produces = "text/event-stream")
    public Flux<ServerSentEvent<?>> subscribe(@RequestParam Long userId) {
        return timelineService.subscribe(userId);
    }

    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestParam("userId") Long userId,
                                             @RequestParam("file") MultipartFile file) {
        timelineService.createPost(userId, file);
        return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{uniqueId}/{fileName}/audio/{fileType}/stream")
    public ResponseEntity<Resource> streamAudioFile(
            @PathVariable String uniqueId,
            @PathVariable String fileName,
            @PathVariable String fileType,
            @RequestHeader HttpHeaders headers) {
        try {
            PostAudioFile audioFile = postService.getAudioFileByUniqueId(uniqueId);
            Resource audioResource = new ByteArrayResource(audioFile.getData());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType("audio/" + fileType));
            responseHeaders.setContentLength(audioResource.contentLength());

            return new ResponseEntity<>(audioResource, responseHeaders, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error fetching audio file", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
