package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.NotificationType;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;


    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder)
    {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
        if(user==null)
        {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public ApplicationUser getUserByRefreshId(String refreshId)
    {

        ApplicationUser user = userRepo.findByRefreshId(refreshId).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
        if(user==null)
        {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    public ApplicationUser getUserByUsername(String username)
    {
        ApplicationUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
        if(user==null)
        {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public ApplicationUser loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("email is not valid"));

    }

    public boolean emailExist(String email)
    {
        return userRepo.findByEmail(email).isPresent();
    }

    public boolean usernameExist(String username)
    {
        return userRepo.findByUsername(username).isPresent();
    }


    public void saveUser(ApplicationUser user)
    {
        userRepo.save(user);
    }


    // search for users by username, firstname, or lastname
    public List<ApplicationUser> searchUsers(String keyword) {
        String query = "%" + keyword + "%"; // Add '%' wildcard to search for any occurrence of the keyword

        return userRepo.searchUsers(query);
    }



}
