package com.innerspaces.innerspace;

import com.innerspaces.innerspace.models.Role;
import com.innerspaces.innerspace.repositories.RoleRepository;
import com.innerspaces.innerspace.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InnerspaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnerspaceApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleRepository roleRepo){
        return args -> {
            roleRepo.save(new Role(1, "USER"));

//            ApplicationUser user = new ApplicationUser();
//            UserProfile profile = new UserProfile();
//            profile.setBio("This is my cool bio yaay");
//            profile.setPrivate(true);
//            user.setFirstName("Ahmed");
//            user.setLast_name("Rayan");
//            user.setCreatedAt(Date.valueOf(LocalDate.now()));
//            userService.registerUser(user, profile);
        };
    }



}
