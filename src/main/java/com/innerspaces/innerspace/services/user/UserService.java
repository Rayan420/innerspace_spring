package com.innerspaces.innerspace.services.user;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;



    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder)
    {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("In the user details service");
        System.out.println(userRepo.findByUsername(username).get());

        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
    }

    public ApplicationUser loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("email is not valid"));

    }
}
