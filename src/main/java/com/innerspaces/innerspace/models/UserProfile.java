package com.innerspaces.innerspace.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profile")
public class UserProfile {



    // class attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private ApplicationUser user;

    @ManyToMany
    @JoinTable(name = "user_followers",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<ApplicationUser> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_following",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<ApplicationUser> following = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<FollowRequest> followRequests = new HashSet<>();





    // class constructors

    public UserProfile() {
        super();
    }



    // Getters and setters

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt() {
        this.createdAt = LocalDate.now();
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated() {
        this.lastUpdated = LocalDate.now();
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

    public Set<ApplicationUser> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<ApplicationUser> followers) {
        this.followers = followers;
    }

    public Set<ApplicationUser> getFollowing() {
        return following;
    }

    public void setFollowing(Set<ApplicationUser> following) {
        this.following = following;
    }

    public Set<FollowRequest> getFollowRequests() {
        return followRequests;
    }

    public void setFollowRequests(Set<FollowRequest> followRequests) {
        this.followRequests = followRequests;
    }
}
