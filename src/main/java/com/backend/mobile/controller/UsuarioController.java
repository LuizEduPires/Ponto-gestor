package com.backend.mobile.controller;

import com.backend.mobile.models.Usuario;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final Firestore firestore;

    public UsuarioController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping
    public String criarUsuario(@RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> futuro = firestore.collection("usuarios").add(usuario);
        return "Usuário cadastrado com sucesso! ID: " + futuro.get().getId();
    }

    @GetMapping
    public List<Usuario> listarUsuarios() throws ExecutionException, InterruptedException {
        List<Usuario> listaDeUsuarios = new ArrayList<>();

        ApiFuture<QuerySnapshot> futuro = firestore.collection("usuarios").get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        for (QueryDocumentSnapshot documento : documentos) {
            Usuario usuario = documento.toObject(Usuario.class);
            listaDeUsuarios.add(usuario);
        }

        return listaDeUsuarios;
    }

    @GetMapping("/{email}")
    public Usuario buscarPorEmail(@PathVariable String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> futuro = firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get();

        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        if (!documentos.isEmpty()) {
            return documentos.get(0).toObject(Usuario.class);
        } else {
            return null;
        }
    }
}
