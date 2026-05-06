package com.costura.repository;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsável por inicializar o Firebase Admin SDK.
 *
 * Como obter o arquivo serviceAccountKey.json:
 *   1. Acesse console.firebase.google.com
 *   2. Configurações do projeto → Contas de serviço
 *   3. Clique em "Gerar nova chave privada"
 *   4. Salve o arquivo como src/main/resources/serviceAccountKey.json
 */
public class FirebaseConfig {

    private static Firestore instancia;

    private FirebaseConfig() {}

    /**
     * Retorna a instância única do Firestore (padrão Singleton).
     */
    public static synchronized Firestore getFirestore() {
        if (instancia == null) {
            inicializar();
        }
        return instancia;
    }

    private static void inicializar() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream credencial = carregarCredencial();

                FirebaseOptions opcoes = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credencial))
                        // Substitua pelo ID do seu projeto Firebase
                        .setProjectId("seu-projeto-firebase-id")
                        .build();

                FirebaseApp.initializeApp(opcoes);
                System.out.println("[Firebase] Inicializado com sucesso.");
            }

            instancia = FirestoreClient.getFirestore();

        } catch (IOException e) {
            throw new RuntimeException(
                "[Firebase] Falha ao inicializar. " +
                "Verifique se o arquivo serviceAccountKey.json está em src/main/resources/", e
            );
        }
    }

    /**
     * Tenta carregar a chave de serviço primeiro do classpath,
     * depois de um caminho absoluto definido pela variável de ambiente
     * GOOGLE_APPLICATION_CREDENTIALS.
     */
    private static InputStream carregarCredencial() throws IOException {
        // 1ª tentativa: classpath (ideal para desenvolvimento)
        InputStream recurso = FirebaseConfig.class
                .getClassLoader()
                .getResourceAsStream("serviceAccountKey.json");

        if (recurso != null) {
            System.out.println("[Firebase] Credencial carregada do classpath.");
            return recurso;
        }

        // 2ª tentativa: variável de ambiente (ideal para produção)
        String caminhoEnv = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (caminhoEnv != null && !caminhoEnv.isBlank()) {
            System.out.println("[Firebase] Credencial carregada de: " + caminhoEnv);
            return new FileInputStream(caminhoEnv);
        }

        throw new IOException(
            "Arquivo serviceAccountKey.json não encontrado. " +
            "Coloque-o em src/main/resources/ ou defina a variável " +
            "GOOGLE_APPLICATION_CREDENTIALS."
        );
    }
}
