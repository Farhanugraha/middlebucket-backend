package com.middle_bucket.middlebucket.seeder;


import com.middle_bucket.middlebucket.entity.Role;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
    }

    private void seedUsers() {

        if (userRepository.findByEmail("manager@middlebucket.com").isPresent()) {
            System.out.println("Seeder: Data sudah ada, skip seeding.");
            return;
        }

        // Manager
        User manager = new User();
        manager.setName("Admin Manager");
        manager.setEmail("manager@middlebucket.com");
        manager.setPhone("081111111111");
        manager.setPassword(passwordEncoder.encode("Password123!"));
        manager.setRole(Role.MANAGER);
        manager.setCreatedAt(LocalDateTime.now());
        userRepository.save(manager);

        System.out.println("Seeder: Users berhasil dibuat.");
    }
}
