package com.korarwanda.kora;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class KoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(KoraApplication.class, args);
    }

    @Bean
    public CommandLineRunner openBrowser() {
        return args -> {
            System.out.println("Wait 5 seconds for the system to fully initialize...");
            Thread.sleep(5000); // 5 seconds to be safe
            String url = "http://localhost:8080/login.html";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
            System.out.println("System launched at: " + url);
        };
    }
}
