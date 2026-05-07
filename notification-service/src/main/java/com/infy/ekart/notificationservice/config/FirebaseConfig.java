package com.infy.ekart.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            // Initialize Firebase FIRST inside the bean method
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;

                // Try classpath
                try {
                    ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                    serviceAccount = resource.getInputStream();
                    log.info("✅ Found firebase-service-account.json in classpath");
                } catch (Exception e) {
                    // Try absolute path
                    String path = System.getProperty("user.dir") + "/src/main/resources/firebase-service-account.json";
                    File file = new File(path);
                    if (file.exists()) {
                        serviceAccount = new FileInputStream(file);
                        log.info("✅ Found at: {}", path);
                    } else {
                        log.error("❌ firebase-service-account.json NOT FOUND at: {}", path);
                        throw new RuntimeException("firebase-service-account.json not found! Download from Firebase Console.");
                    }
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Initialized Successfully!");
            }

            return FirebaseAuth.getInstance();

        } catch (Exception e) {
            log.error("❌ Firebase initialization failed: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}