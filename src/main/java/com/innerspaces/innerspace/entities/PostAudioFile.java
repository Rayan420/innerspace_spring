package com.innerspaces.innerspace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostAudioFile {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @Lob
    private byte[] data;

    @JsonIgnore
    private String filename;
    @JsonIgnore
    private String contentType;



}
