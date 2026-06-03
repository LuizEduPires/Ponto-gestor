package com.backend.mobile.controller;

import com.backend.mobile.models.Cliente;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final Firestore firestore;

    public ClienteController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping
    public String criarCliente(@RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> futuro = firestore.collection("clientes").add(cliente);
        DocumentReference documento = futuro.get();
        return "Cliente cadastrado com sucesso! ID: " + documento.getId();
    }

    @GetMapping
    public List<Cliente> listarClientes() throws ExecutionException, InterruptedException {
        List<Cliente> listaDeClientes = new ArrayList<>();

        ApiFuture<QuerySnapshot> futuro = firestore.collection("clientes").get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        for (QueryDocumentSnapshot documento : documentos) {
            Cliente cliente = documento.toObject(Cliente.class);
            cliente.setId(documento.getId());
            listaDeClientes.add(cliente);
        }

        return listaDeClientes;
    }
}