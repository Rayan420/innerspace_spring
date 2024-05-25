package com.innerspaces.innerspace;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.Role;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class InnerspaceApplication {

    public static void main(String[] args) {

        ApplicationContext ctx =  SpringApplication.run(InnerspaceApplication.class, args);
    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            Role role = new Role();
            role.setAuthority("USER");
            roleRepository.save(role);

//            // Sample usernames
//            String[] usernames = {"john_doe", "sarah_smith", "mike_jones", "emily_brown", "alex_williams", "chris_taylor"};
//
//            // Create 50 users
//            for (int i = 0; i < 50; i++) {
//                ApplicationUser user = new ApplicationUser();
//                int index = i % usernames.length; // Index for selecting usernames cyclically
//                user.setUsername(usernames[index] + i); // Append index for uniqueness
//                user.setEmail("user" + i + "@example.com");
//                user.setFirstName("First" + i);
//                user.setLastName("Last" + i);
//                user.setDateOfBirth(Date.valueOf(LocalDate.now().minusYears(20 + i)));
//                user.setPassword("password" + i);
//                user.setLastLogin(); // Set last login timestamp
//
//                // Set default roles
//                Set<Role> roles = new HashSet<>();
//                roles.add(role);
//                user.setAuthorities(roles);
//
//                userRepository.save(user);
//            }
//
//            // Create some users with duplicate first and last names
//            for (int i = 0; i < 10; i++) {
//                ApplicationUser user = new ApplicationUser();
//                user.setUsername("user_duplicate" + i);
//                user.setEmail("user_duplicate" + i + "@example.com");
//                user.setFirstName("Duplicate");
//                user.setLastName("User");
//                user.setDateOfBirth(Date.valueOf(LocalDate.now().minusYears(20 + i)));
//                user.setPassword("password_duplicate" + i);
//                user.setLastLogin(); // Set last login timestamp
//
//                // Set default roles
//                Set<Role> roles = new HashSet<>();
//                roles.add(role);
//                user.setAuthorities(roles);
//
//                userRepository.save(user);
//            }
        };
    }


}
