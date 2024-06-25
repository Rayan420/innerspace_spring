package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.entities.PostAudioFile;
import com.innerspaces.innerspace.services.user.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@CrossOrigin("*")
@Slf4j
@RequestMapping("/listen")
public class AudioController {
    private final PostService postService;

    public AudioController(PostService postService) {
        this.postService = postService;
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
