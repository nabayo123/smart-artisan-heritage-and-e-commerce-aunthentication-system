package com.korarwandasystem.korarwanda.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BasicController {

    @GetMapping("/status")
    public String getStatus() {
        return "Kora Rwanda Backend - Spring Boot Setup Complete";
    }

    @GetMapping("/health")
    public String getHealth() {
        return "Application is running successfully";
    }
}
