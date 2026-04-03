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
            System.out.println("Wait 2 seconds to start browser...");
            Thread.sleep(2000);
            String url = "http://localhost:8080";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        };
    }
}
