package com.innerspaces.innerspace.models.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateDTO {

    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
    private String coverImageUrl;
}
