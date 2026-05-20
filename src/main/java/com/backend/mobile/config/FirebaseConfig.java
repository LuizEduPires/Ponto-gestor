package com.backend.mobile.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getFirestore() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            try {
                InputStream serviceAccount;

                String firebaseEnv = System.getenv("FIREBASE_CREDENTIALS");

                if (firebaseEnv != null && !firebaseEnv.isEmpty()) {
                    serviceAccount = new ByteArrayInputStream(firebaseEnv.getBytes(StandardCharsets.UTF_8));
                    System.out.println("☁️ Lendo credenciais do Firebase via Variável de Ambiente (Produção).");
                } else {
                    serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");
                    System.out.println("💻 Lendo credenciais do Firebase via Arquivo Local.");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println(" Firebase inicializado com sucesso!");

            } catch (Exception e) {
                System.err.println(" ERRO ao carregar as credenciais do Firebase.");
                throw e;
            }
        }

        return FirestoreClient.getFirestore();
    }
}