package com.backend.mobile.controller;

import com.backend.mobile.models.Pedido;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final Firestore firestore;

    public PedidoController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping
    public String salvarPedidoCompleto(@RequestBody Pedido pedido) throws ExecutionException, InterruptedException {


        ApiFuture<DocumentReference> futuro = firestore.collection("pedidos").add(pedido);

        return "Pedido criado com sucesso no Firebase! ID: " + futuro.get().getId();
    }
    @GetMapping
    public List<Pedido> listarTodos() throws ExecutionException, InterruptedException {
        List<Pedido> listaDePedidos = new ArrayList<>();

        ApiFuture<QuerySnapshot> futuro = firestore.collection("pedidos").get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        for (QueryDocumentSnapshot documento : documentos) {
            // O Firebase transforma o JSON do banco direto na sua classe Java!
            Pedido pedido = documento.toObject(Pedido.class);

            pedido.setId(documento.getId());

            listaDePedidos.add(pedido);
        }

        return listaDePedidos;
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {

        DocumentReference referencia = firestore.collection("pedidos").document(id);
        ApiFuture<DocumentSnapshot> futuro = referencia.get();
        DocumentSnapshot documento = futuro.get();

        if (documento.exists()) {
            Pedido pedido = documento.toObject(Pedido.class);
            pedido.setId(documento.getId());
            return pedido;
        } else {
            return null;
        }
    }
}