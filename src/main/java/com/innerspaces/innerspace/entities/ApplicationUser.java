package com.innerspaces.innerspace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@Entity
@Table(name = "users")
public class ApplicationUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    private Date dateOfBirth;

    @JsonIgnore
    private String password;

    private LocalDate dateJoined = LocalDate.now();
    private String lastLogin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "sender")
    private Set<FollowRequest> sentFollowRequests = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<FollowRequest> receivedFollowRequests = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_join_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    @ManyToMany
    @JoinTable(name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<ApplicationUser> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_following",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<ApplicationUser> following = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotpassword;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;

    private boolean isEnabled;




    // Constructors

    public ApplicationUser() {
        super();
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
    }

    public ApplicationUser(Long userId, String username, String email, String firstName, String lastName, Date dateOfBirth, String password, LocalDate dateJoined, String lastLogin, UserProfile userProfile, Set<FollowRequest> sentFollowRequests, Set<FollowRequest> receivedFollowRequests, Set<Role> authorities, Set<ApplicationUser> followers, Set<ApplicationUser> following) {
        super();
        this.isCredentialsNonExpired = true;
        this.isEnabled = false;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.dateJoined = dateJoined;
        this.lastLogin = lastLogin;
        this.userProfile = userProfile;
        this.sentFollowRequests = sentFollowRequests;
        this.receivedFollowRequests = receivedFollowRequests;
        this.authorities = authorities;
        this.followers = followers;
        this.following = following;
    }
// Getters and setters for all fields

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Set<FollowRequest> getSentFollowRequests() {
        return sentFollowRequests;
    }

    public Set<FollowRequest> getReceivedFollowRequests() {
        return receivedFollowRequests;
    }

    public Set<ApplicationUser> getFollowers() {
        return followers;
    }

    public Set<ApplicationUser> getFollowing() {
        return following;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public void setLastLogin() {
        java.util.Date currentDate = Date.from(Instant.now());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // Adjust timezone if needed
        String formattedDate = sdf.format(currentDate);
        System.out.println("Formatted Date: " + formattedDate); // Print for testing
        this.lastLogin = formattedDate;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setSentFollowRequests(Set<FollowRequest> sentFollowRequests) {
        this.sentFollowRequests = sentFollowRequests;
    }

    public void setReceivedFollowRequests(Set<FollowRequest> receivedFollowRequests) {
        this.receivedFollowRequests = receivedFollowRequests;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public void setFollowers(Set<ApplicationUser> followers) {
        this.followers = followers;
    }

    public void setFollowing(Set<ApplicationUser> following) {
        this.following = following;
    }
}
