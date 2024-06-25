package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.*;
import com.innerspaces.innerspace.models.user.PostDTO;
import com.innerspaces.innerspace.repositories.Timeline.*;
import com.innerspaces.innerspace.repositories.user.UserFollowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostAudioFileRepository postAudioFileRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserFollowingRepository userFollowingRepository;

    @Autowired
    public PostService(PostAudioFileRepository postAudioFileRepository, PostRepository postRepository, UserService userService, UserFollowingRepository userFollowingRepository) {
        this.postAudioFileRepository = postAudioFileRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.userFollowingRepository = userFollowingRepository;
    }

    public List<Post> getPosts(Long userId) {
        return postRepository.findByUserInOrderByTimestampDesc(userId);
    }

    @Transactional(readOnly = true)
    public Flux<PostDTO> getTimeline(Long userId) {
        return Flux.defer(() -> {
            ApplicationUser user = userService.getUserById(userId);
            List<UserFollowing> followings = userFollowingRepository.findByUser(user);

            Set<ApplicationUser> followedUsers = followings.stream()
                    .map(UserFollowing::getFollowing)
                    .collect(Collectors.toSet());
            followedUsers.add(user);

            return Flux.fromIterable(postRepository.findByUserInOrderByTimestampDesc(followedUsers)
                    .stream()
                    .map(PostDTO::fromPost)
                    .collect(Collectors.toList()));
        });
    }

    public Post createPost(Long userId, MultipartFile file, int duration) throws IOException {
        ApplicationUser user = userService.getUserById(userId);
        System.out.println("file name: " + file.getOriginalFilename() + " file type: " + file.getContentType() + " file size: " + file.getSize()
                +  " file resource: " + file.getResource() + " file url: " + file.getOriginalFilename()
        );
        PostAudioFile audioFile = new PostAudioFile();
        audioFile.setData(file.getBytes());
        audioFile.setFilename(file.getOriginalFilename());
        audioFile.setContentType(file.getContentType());
        PostAudioFile savedAudioFile = postAudioFileRepository.save(audioFile);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/listen/")
                .path(savedAudioFile.getId())
                .path("/")
                .path(audioFile.getFilename())
                .path("/")
                .path(audioFile.getContentType())
                .path("/stream")
                .toUriString();
        System.out.println("URL: " + url);
        // check if the url contains 10.0.2.2 if so replace with localhost
        if (url.contains("10.0.2.2")) {
            url = url.replace("10.0.2.2", "localhost");
        }
        Post post = new Post();
        post.setUser(user);
        post.setAudioFile(savedAudioFile);
        post.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
        post.setTimestamp(LocalDateTime.now());
        post.setUrl(url);
        post.setDuration(duration);

        postRepository.save(post);
        return post;
    }

    public PostAudioFile getAudioFileByUniqueId(String uniqueId) {
        return postAudioFileRepository.findById(uniqueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid audio file URL"));
    }

    public Resource getAudioFileResource(Long postId) {
        PostAudioFile audioFile = getAudioFile(postId);
        return new ByteArrayResource(audioFile.getData());
    }

    private PostAudioFile getAudioFile(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        return post.getAudioFile();
    }

    private double calculateDuration(MultipartFile audioData) {
        return 0;
    }
}
