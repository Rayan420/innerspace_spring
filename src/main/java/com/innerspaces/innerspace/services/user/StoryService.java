package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.Story;
import com.innerspaces.innerspace.entities.StoryAudioFile;
import com.innerspaces.innerspace.entities.StoryListen;
import com.innerspaces.innerspace.repositories.Timeline.StoryAudioFileRepository;
import com.innerspaces.innerspace.repositories.Timeline.StoryListenRepository;
import com.innerspaces.innerspace.repositories.Timeline.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final StoryAudioFileRepository storyAudioFileRepository;
    private final StoryListenRepository storyListenRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository, StoryAudioFileRepository storyAudioFileRepository, StoryListenRepository storyListenRepository) {
        this.storyRepository = storyRepository;
        this.storyAudioFileRepository = storyAudioFileRepository;
        this.storyListenRepository = storyListenRepository;
    }


    private StoryAudioFile storeStoryAudioFile(MultipartFile audioFile, LocalDateTime expiration) throws Exception {
        StoryAudioFile file = new StoryAudioFile();
        file.setData(audioFile.getBytes());
        file.setFilename(audioFile.getOriginalFilename());
        file.setContentType(audioFile.getContentType());
        file.setExpiration(expiration);
        return storyAudioFileRepository.save(file);
    }


    public void saveStory(Story story, MultipartFile audioFile) throws Exception {
        StoryAudioFile storedAudioFile = storeStoryAudioFile(audioFile, story.getExpiration());
        story.setAudioFile(storedAudioFile);
        storyRepository.save(story);
    }


    public void saveStoryListen(Story story, ApplicationUser user) {
        StoryListen listen = new StoryListen();
        listen.setStory(story);
        listen.setUser(user);
        storyListenRepository.save(listen);
    }

//    public List<Story> getStories(ApplicationUser user) {
//        Set<ApplicationUser> followedUsers = user.getFollowing();
//        followedUsers.add(user); // Include the user's own stories
//        return storyRepository.findByUserInAndExpirationAfterOrderByTimestampDesc(followedUsers, LocalDateTime.now());
//    }



    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // Run every 12 hours
    public void moveExpiredStories() {
        List<StoryAudioFile> expiredStories = storyAudioFileRepository.findByExpirationBefore(LocalDateTime.now());
        storyAudioFileRepository.deleteAll(expiredStories);
    }

}
