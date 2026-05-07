package com.backend.mobile.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getFirestore() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            try {
                // A MÁGICA MUDA AQUI: Lendo direto da raiz do projeto, ignorando a compilação do Maven
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado com sucesso para o Ponto Gestor!");

            } catch (Exception e) {
                System.err.println("❌ ERRO: O arquivo ainda não foi encontrado. Verifique se o nome está EXATAMENTE igual a 'firebase-service-account.json' e se ele está dentro da pasta 'src/main/resources'");
                throw e; // Trava a inicialização para não rodar o app quebrado
            }
        }

        return FirestoreClient.getFirestore();
    }
}