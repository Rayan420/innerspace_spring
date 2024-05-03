package com.innerspaces.innerspace.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innerspaces.innerspace.entities.ApplicationUser;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
public class UserProfile {



    // class attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;



    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated = LocalDate.now();

    @Column(name = "follower_count", nullable = false)
    private int followerCount = 0;

    @Column(name = "following_count", nullable = false)
    private int followingCount = 0;

    @Column(name = "owned_space_count", nullable = false)
    private int ownedSpaceCount = 0;

    @Column(name = "followed_space_count", nullable = false)
    private int followedSpaceCount = 0;



    // class constructors

    public UserProfile() {
        super();
    }

    public UserProfile(byte[] profilePicture, String bio) {
        this.profilePicture = profilePicture;
        this.bio = bio;
    }
    // Getters and setters

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }


    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getOwnedSpaceCount() {
        return ownedSpaceCount;
    }

    public void setOwnedSpaceCount(int ownedSpaceCount) {
        this.ownedSpaceCount = ownedSpaceCount;
    }

    public int getFollowedSpaceCount() {
        return followedSpaceCount;
    }

    public void setFollowedSpaceCount(int followedSpaceCount) {
        this.followedSpaceCount = followedSpaceCount;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public void setLastUpdated() {
        this.lastUpdated = LocalDate.now();
    }
}
