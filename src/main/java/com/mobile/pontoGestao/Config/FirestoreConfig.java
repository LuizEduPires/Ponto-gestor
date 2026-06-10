package com.mobile.pontoGestao.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirestoreConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount;

        // Caminho padrão onde o Render injeta os "Secret Files"
        File renderSecret = new File("/etc/secrets/servicefirebase.json");

        if (renderSecret.exists()) {
            // Se existir, estamos rodando na nuvem do Render
            serviceAccount = new FileInputStream(renderSecret);
            System.out.println("✅ Firebase conectado usando o cofre do Render!");
        } else {
            // Se não existir, estamos rodando localmente no seu computador (IntelliJ)
            serviceAccount = getClass().getClassLoader().getResourceAsStream("servicefirebase.json");

            // Trava de segurança extra para avisar se o arquivo realmente sumiu
            if (serviceAccount == null) {
                throw new RuntimeException("❌ ERRO GRAVE: Arquivo servicefirebase.json não encontrado em nenhum lugar!");
            }
            System.out.println("✅ Firebase conectado usando o arquivo local!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}