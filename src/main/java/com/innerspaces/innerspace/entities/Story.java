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
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_file_id")
    private StoryAudioFile audioFile;

    private LocalDateTime timestamp = LocalDateTime.now();
    private LocalDateTime expiration = timestamp.plusDays(1);

    private String profilePictureUrl;
    private String coverPictureUrl;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryListen> listens;

}
