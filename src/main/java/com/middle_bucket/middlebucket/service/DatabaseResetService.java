package com.middle_bucket.middlebucket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseResetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void resetDatabase() {
        try {
            System.out.println("🔄 Memulai reset database...");

            // 1. Disable foreign key checks
            disableForeignKeyChecks();

            // 2. Bersihkan data
            jdbcTemplate.execute("DELETE FROM user");
            System.out.println("  ✓ Data user cleared");

            // 3. Reset auto increment
            jdbcTemplate.execute("ALTER TABLE user AUTO_INCREMENT = 1");
            System.out.println("  ✓ Auto increment reset");

            // 4. Enable foreign key checks
            enableForeignKeyChecks();

            // 5. Insert seed data
            insertSeedData();

            System.out.println("✅ Database berhasil direset!");

        } catch (Exception e) {
            System.err.println("❌ Error reset database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal reset database", e);
        }
    }

    private void disableForeignKeyChecks() {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        } catch (Exception e) {
            // Ignore
        }
    }

    private void enableForeignKeyChecks() {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (Exception e) {
            // Ignore
        }
    }

    private void insertSeedData() {
        System.out.println("📝 Menambahkan seed data...");

        // Insert user dengan kolom password_hash (sesuai entity)
        String insertUsers = """
            INSERT INTO user (id, name, email, phone, password_hash, role, created_at) VALUES
            (1, 'Tasya', 'tasya@example.com', NULL, '$2a$10$CvUwCcrn9ionSsXf1QSLUeRFgDVgtMXlJ5hLGOjpiI3Z/VD/.mnQui', 'STAFF', NOW()),
            (2, 'Farhan Nugraha', 'farhanoegrahaa@gmail.com', NULL, '$2a$10$08mvJyMXxCcQEXZcDc6CXv05mmLMs8K/TWpYsNlao3xnYDGq2/EZ4C', 'STAFF', NOW()),
            (3, 'Admin Manager', 'manager@middlebucket.com', NULL, '$2a$10$a/Bp9Rn3f5ghZ697kQjpreMbnNEd8dUCGo.ZacFqcs3jKJcJpFLiy', 'MANAGER', NOW())
            ON DUPLICATE KEY UPDATE 
            name = VALUES(name), 
            email = VALUES(email),
            phone = VALUES(phone),
            password_hash = VALUES(password_hash),
            role = VALUES(role);
        """;

        try {
            jdbcTemplate.execute(insertUsers);
            System.out.println("  ✓ Seed data inserted successfully!");
        } catch (Exception e) {
            System.err.println("  ✗ Failed insert seed data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}