package com.hcmute.shopfee.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        log.info("Connecting to Firebase admin...");
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebasePrivateKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            FirebaseApp defaultApp = firebaseApps.isEmpty() ? FirebaseApp.initializeApp(options) : firebaseApps.get(0);

            log.info("Connected to Firebase admin...");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/firebasePrivateKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        FirebaseApp defaultApp = firebaseApps.isEmpty() ? FirebaseApp.initializeApp(options) : firebaseApps.get(0);
        return FirebaseMessaging.getInstance(defaultApp);
    }
}
