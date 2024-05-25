package com.innerspaces.innerspace.models.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowDto {

     int followerCount;
     int followingCount;


}
