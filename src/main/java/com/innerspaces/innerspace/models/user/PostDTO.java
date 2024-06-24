package com.innerspaces.innerspace.models.user;

import com.innerspaces.innerspace.entities.Post;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class PostDTO {

    private Long id;
    private String userName;
    private String name;
    private String audioUrl;
    private String profileImageUrl;
    private String timestamp;
    private String coverImageUrl;

    public PostDTO(Long id, String userName, String name, String audioUrl, String profileImageUrl, String timestamp, String coverImageUrl) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.audioUrl = audioUrl;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
        this.coverImageUrl = coverImageUrl;
    }

    public static PostDTO fromPost(Post post) {
        return new PostDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getUser().getFirstName() + " " + post.getUser().getLastName(),
                post.getUrl(),
                post.getProfileImageUrl(),
                post.getTimestamp().toString(),
                post.getCoverImageUrl()
        );
    }

    // Method to create an empty PostDTO
    public static PostDTO empty() {
        return new PostDTO(0L, "", "", "", "", "", "");
    }
}
