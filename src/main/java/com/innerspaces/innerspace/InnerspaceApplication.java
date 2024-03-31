package com.innerspaces.innerspace;

import com.innerspaces.innerspace.models.user.Role;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
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
    CommandLineRunner run(RoleRepository role) {
        return args -> {
            Role roles = new Role();
            roles.setAuthority("USER");
            role.save(roles);
        };
    }


}
