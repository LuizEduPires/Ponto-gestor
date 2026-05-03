package com.backend.mobile.controller;

import com.backend.mobile.models.Cliente;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final Firestore firestore;

    public ClienteController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping
    public String salvarCliente(@RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        Map<String, Object> dadosCliente = new HashMap<>();
        dadosCliente.put("nome", cliente.getNome());
        dadosCliente.put("telefone", cliente.getTelefone());
        dadosCliente.put("data_cadastro", System.currentTimeMillis());

        ApiFuture<DocumentReference> resultado = firestore.collection("clientes").add(dadosCliente);

        return "Cliente " + cliente.getNome() + " salvo com sucesso no ID: " + resultado.get().getId();
    }
}