package com.backend.mobile.controller;

import com.backend.mobile.models.Cliente;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private Firestore firestore;

    // 1. Rota para CRIAR um novo cliente
    @PostMapping
    public String criarCliente(@RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        // Salva na coleção "clientes"
        ApiFuture<DocumentReference> futuro = firestore.collection("clientes").add(cliente);
        DocumentReference documento = futuro.get();
        return "Cliente cadastrado com sucesso! ID: " + documento.getId();
    }

    // 2. Rota para LISTAR TODOS os clientes
    @GetMapping
    public List<Cliente> listarClientes() throws ExecutionException, InterruptedException {
        List<Cliente> listaDeClientes = new ArrayList<>();

        // Pede todos os documentos da coleção "clientes"
        ApiFuture<QuerySnapshot> futuro = firestore.collection("clientes").get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        for (QueryDocumentSnapshot documento : documentos) {
            Cliente cliente = documento.toObject(Cliente.class);
            cliente.setId(documento.getId()); // Pega o ID de fora e joga pra dentro do objeto
            listaDeClientes.add(cliente);
        }

        return listaDeClientes;
    }
}