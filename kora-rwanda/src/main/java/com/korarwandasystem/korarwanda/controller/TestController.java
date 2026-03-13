package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.User;
import com.korarwandasystem.korarwanda.model.UserType;
import com.korarwandasystem.korarwanda.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        logger.info("=== Testing Database Status ===");
        
        long userCount = userRepository.count();
        List<User> users = userRepository.findAll();
        
        Map<String, Object> response = Map.of(
            "timestamp", LocalDateTime.now(),
            "database", "H2 File-Based",
            "userCount", userCount,
            "users", users,
            "message", "Database connection successful"
        );
        
        logger.info("Found {} users in database", userCount);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createTestUser() {
        logger.info("=== Creating Test User ===");
        
        User testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test.user." + System.currentTimeMillis() + "@test.com");
        testUser.setPassword("password123");
        testUser.setUserType(UserType.CUSTOMER);
        testUser.setEnabled(true);
        testUser.setPhoneNumber("+250788999999");
        
        User savedUser = userRepository.save(testUser);
        
        logger.info("Saved user with ID: {}", savedUser.getUserId());
        
        Map<String, Object> response = Map.of(
            "timestamp", LocalDateTime.now(),
            "message", "User created successfully",
            "userId", savedUser.getUserId(),
            "email", savedUser.getEmail(),
            "totalUsers", userRepository.count()
        );
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear-users")
    public ResponseEntity<Map<String, Object>> clearTestData() {
        logger.info("=== Clearing Test Data ===");
        
        long beforeCount = userRepository.count();
        
        // Only delete test users (not admin or sample data)
        List<User> testUsers = userRepository.findByEmailContaining("@test.com");
        userRepository.deleteAll(testUsers);
        
        long afterCount = userRepository.count();
        
        Map<String, Object> response = Map.of(
            "timestamp", LocalDateTime.now(),
            "message", "Test data cleared",
            "usersDeleted", testUsers.size(),
            "beforeCount", beforeCount,
            "afterCount", afterCount
        );
        
        logger.info("Deleted {} test users", testUsers.size());
        
        return ResponseEntity.ok(response);
    }
}
