package com.innerspaces.innerspace.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    public Long userId;

    public String username;
    public String firstName;
    public String lastName;

    public String bio;

    public Date dob;

    public Date joinDate;

    public String profileImageUrl;

    public boolean isPrivate;

    public int followerCount;

    public int followingCount;

    public int ownedSpaceCount;


}
