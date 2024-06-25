package com.innerspaces.innerspace.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_file_id")
    private PostAudioFile audioFile; // Changed to reference PostAudioFile


    private int duration;
    // This field can be generated or set to provide the URL for streaming
    private String url;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String profileImageUrl;
    private String coverImageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostListen> listens;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReply> replies;

   
}
