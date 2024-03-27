package com.innerspaces.innerspace;

import com.innerspaces.innerspace.models.ApplicationUser;
import com.innerspaces.innerspace.models.Role;
import com.innerspaces.innerspace.repositories.RoleRepository;
import com.innerspaces.innerspace.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;

@SpringBootApplication
public class InnerspaceApplication {

    public static void main(String[] args) {

        SpringApplication.run(InnerspaceApplication.class, args);

    }

    @Bean
    CommandLineRunner runner(UserRepository userRepo, RoleRepository roleRepo){
        return args -> {
            roleRepo.save(new Role(1, "USER"));
            ApplicationUser u;
            u = new ApplicationUser();
            u.setFirstName("ahmed");
            u.setLast_name("rayan");
            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepo.findByAuthority("USER").get());
            u.setAuthorities(roles);
            u.setCreatedAt(Date.valueOf(LocalDate.now()));
            userRepo.save(u);

        };

    }


}
