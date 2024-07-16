package com.innerspaces.innerspace.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class CoverImage {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    private String id;

    @JsonIgnore
    private String fileName;
    @JsonIgnore

    private String fileType;

    @JsonIgnore
    @Lob
    private byte[] cover_image;

    @JsonIgnore
    @OneToOne
    private UserProfile profile;

    public CoverImage(String fileName, String fileType, byte[] cover_image) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.cover_image = cover_image;
    }


}
