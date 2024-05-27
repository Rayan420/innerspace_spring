package com.innerspaces.innerspace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@Entity
@Data
@Table(name = "users")
@Getter
@Setter
public class ApplicationUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
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
    @JsonManagedReference
    private UserProfile userProfile;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_join_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"followers", "following"}) // Ignore serialization of followers and following
    private Set<ApplicationUser> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"followers", "following"}) // Ignore serialization of followers and following
    private Set<ApplicationUser> following = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotpassword;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    @Column(name = "refresh_id", unique = true)
    @JsonIgnore
    private String refreshId;





    // Constructors

    public ApplicationUser() {
        super();
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
    }

    public ApplicationUser(Long userId, String username, String email, String firstName, String lastName, Date dateOfBirth, String password, LocalDate dateJoined, String lastLogin, UserProfile userProfile, Set<Role> authorities, Set<ApplicationUser> followers, Set<ApplicationUser> following) {
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
        this.authorities = authorities;
        this.followers = followers;
        this.following = following;
    }
// Getters and setters for all fields



    public void setLastLogin() {
        java.util.Date currentDate = Date.from(Instant.now());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // Adjust timezone if needed
        String formattedDate = sdf.format(currentDate);
        System.out.println("Formatted Date: " + formattedDate); // Print for testing
        this.lastLogin = formattedDate;
    }



    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ApplicationUser that = (ApplicationUser) obj;
        return userId != null && userId.equals(that.userId);
    }
    @Override
    public String toString() {
        return "ApplicationUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                // Include other fields as needed
                '}';
    }


}