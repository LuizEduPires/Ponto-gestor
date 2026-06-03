package com.backend.mobile.controller;

import com.backend.mobile.models.Pagamento;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final Firestore firestore;

    public PagamentoController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping
    public String criarPagamento(@RequestBody Pagamento pagamento) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> futuro = firestore.collection("pagamentos").add(pagamento);
        return "Pagamento registrado com sucesso! ID: " + futuro.get().getId();
    }

    @GetMapping
    public List<Pagamento> listarPagamentos() throws ExecutionException, InterruptedException {
        List<Pagamento> listaDePagamentos = new ArrayList<>();

        ApiFuture<QuerySnapshot> futuro = firestore.collection("pagamentos").get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        for (QueryDocumentSnapshot documento : documentos) {
            Pagamento pagamento = documento.toObject(Pagamento.class);
            pagamento.setId(documento.getId());
            listaDePagamentos.add(pagamento);
        }

        return listaDePagamentos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable String id) throws ExecutionException, InterruptedException {
        DocumentReference referencia = firestore.collection("pagamentos").document(id);
        ApiFuture<DocumentSnapshot> futuro = referencia.get();
        DocumentSnapshot documento = futuro.get();

        if (documento.exists()) {
            Pagamento pagamento = documento.toObject(Pagamento.class);
            pagamento.setId(documento.getId());
            return ResponseEntity.ok(pagamento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
