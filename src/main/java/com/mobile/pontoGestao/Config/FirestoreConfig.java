package com.mobile.pontoGestao.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirestoreConfig {

    private static final Logger log =
            LoggerFactory.getLogger(FirestoreConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {

        FirebaseOptions options;

        File renderSecret =
                new File("/etc/secrets/servicefirebase.json");

        if (renderSecret.exists()) {

            try (InputStream serviceAccount =
                         new FileInputStream(renderSecret)) {

                options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(
                                        serviceAccount
                                )
                        )
                        .build();
            }

            log.info(
                    "Firebase conectado usando o Secret File do Render."
            );

        } else {

            try (InputStream serviceAccount =
                         getClass()
                                 .getClassLoader()
                                 .getResourceAsStream(
                                         "servicefirebase.json"
                                 )) {

                if (serviceAccount == null) {
                    throw new IllegalStateException(
                            "Arquivo servicefirebase.json não encontrado."
                    );
                }

                options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(
                                        serviceAccount
                                )
                        )
                        .build();
            }

            log.info(
                    "Firebase conectado usando o arquivo local."
            );
        }

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore(
            FirebaseApp firebaseApp
    ) {

        return FirestoreClient.getFirestore(firebaseApp);
    }
}