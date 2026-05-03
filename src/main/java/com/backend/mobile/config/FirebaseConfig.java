package com.backend.mobile.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getFirestore() throws IOException {
        // Verifica se o Firebase já não foi iniciado antes
        if (FirebaseApp.getApps().isEmpty()) {

            // Tenta achar o arquivo na pasta resources
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

            if (serviceAccount == null) {
                // Se não achar o arquivo, ele PARA o aplicativo e te avisa claramente o porquê
                throw new RuntimeException("❌ ERRO CRÍTICO: Arquivo 'firebase-service-account.json' não foi encontrado na pasta 'src/main/resources'!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado com sucesso para o Ponto Gestor!");
        }

        // Retorna a conexão com o banco de dados pronta para uso
        return FirestoreClient.getFirestore();
    }
}