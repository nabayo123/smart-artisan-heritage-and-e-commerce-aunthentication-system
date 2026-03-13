package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.User;
import com.korarwandasystem.korarwanda.model.UserType;
import com.korarwandasystem.korarwanda.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceExample {

    private final UserRepository userRepository;

    public UserServiceExample(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Example: This will trigger 409 Conflict (duplicate email)
    public User createUserWithDuplicateEmail() {
        User user1 = new User();
        user1.setEmail("duplicate@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setUserType(UserType.CUSTOMER);
        userRepository.save(user1);

        // This will cause DataIntegrityViolationException
        User user2 = new User();
        user2.setEmail("duplicate@example.com"); // Same email
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setUserType(UserType.CUSTOMER);
        
        return userRepository.save(user2); // Throws exception
    }

    // Example: This will trigger 400 Bad Request (validation)
    public User createUserWithInvalidData() {
        User user = new User();
        user1.setEmail(""); // Invalid - @NotBlank will fail
        user1.setFirstName(""); // Invalid - @NotBlank will fail
        user1.setPassword("123"); // Invalid - @Size(min=6) will fail
        user1.setUserType(null); // Invalid - @NotNull will fail
        
        return userRepository.save(user1);
    }

    // Example: This will trigger 404 Not Found
    public User getUserById(Long userId) {
        if (userId == 99999L) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Example: This will trigger 500 Internal Server Error
    public User causeServerError() {
        throw new RuntimeException("Database connection failed: Connection timeout");
    }
}
