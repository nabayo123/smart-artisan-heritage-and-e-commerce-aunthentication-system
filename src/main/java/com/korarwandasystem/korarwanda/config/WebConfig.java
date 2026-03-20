package com.korarwandasystem.korarwanda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redircct the home URL straight to the register page
        registry.addViewController("/").setViewName("redirect:/register.html");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        try {
            // This tells Windows to open the default browser when the app gets fully started
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/register.html");
        } catch (IOException e) {
            System.err.println("Could not open browser automatically: " + e.getMessage());
        }
    }
}
