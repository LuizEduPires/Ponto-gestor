package com.mobile.pontoGestao.Services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.ClienteRequest;
import com.mobile.pontoGestao.Dtos.Request.ClienteUpdateRequest;
import com.mobile.pontoGestao.Dtos.Response.ClienteResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoCliente;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;
import com.mobile.pontoGestao.Mappers.ClienteMapper;
import com.mobile.pontoGestao.Models.Clientes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ClientesService {

    private final Firestore firestore;
    private final ClienteMapper clienteMapper;

    public ClienteResponse criarCliente(ClienteRequest request) {

        Clientes cliente = clienteMapper.toModel(request);

        firestore.collection("clientes")
                .document(cliente.getId())
                .set(cliente);

        return clienteMapper.toResponse(cliente);
    }

    public ClienteResponse getCliente(String id)
            throws ExecutionException, InterruptedException {

        DocumentSnapshot snapshot = getDocumentSnapshot(id);

        Clientes cliente = snapshot.toObject(Clientes.class);

        return clienteMapper.toResponse(cliente);
    }

    public List<ClienteResponse> getClientes(
            String nome,
            String telefone,
            OrdenacaoCliente ordenacao
    ) throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = firestore.collection("clientes")
                .get()
                .get();

        Comparator<ClienteResponse> comparator =
                Comparator.comparing(ClienteResponse::nome);

        if (ordenacao != null) {
            comparator = switch (ordenacao) {
                case CRIACAO ->
                        Comparator.comparing(ClienteResponse::dataCriacao).reversed();
                case NOME ->
                        Comparator.comparing(ClienteResponse::nome);
            };
        }

        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Clientes.class))
                .map(clienteMapper::toResponse)
                .filter(cliente ->
                        nome == null ||
                        (
                                cliente.nome() != null &&
                                cliente.nome()
                                        .toLowerCase()
                                        .contains(nome.toLowerCase())
                        )
                )
                .filter(cliente ->
                        telefone == null ||
                        (
                                cliente.telefone() != null &&
                                cliente.telefone()
                                        .toLowerCase()
                                        .contains(telefone.toLowerCase())
                        )
                )
                .sorted(comparator)
                .toList();
    }

    public ClienteResponse atualizarCliente(
        String id,
        ClienteUpdateRequest request
    ) throws ExecutionException, InterruptedException {

        if (
                request.nome() == null &&
                request.telefone() == null &&
                request.descricao() == null
        ) {
            throw new IllegalArgumentException(
                    "Pelo menos um campo deve ser informado"
            );
        }

        DocumentSnapshot document = getDocumentSnapshot(id);

        Clientes cliente = document.toObject(Clientes.class);

        clienteMapper.updateCliente(request, cliente);

        firestore.collection("clientes")
                .document(cliente.getId())
                .set(cliente);

        return clienteMapper.toResponse(cliente);
    }

    public void deletarCliente(String id) throws ExecutionException, InterruptedException {
        getDocumentSnapshot(id);

        firestore.collection("clientes")
                .document(id)
                .delete();
    }

    private DocumentSnapshot getDocumentSnapshot(String id)
            throws ExecutionException, InterruptedException {

        DocumentSnapshot snapshot = firestore.collection("clientes")
                .document(id)
                .get()
                .get();

        if (!snapshot.exists()) {
            throw new EntityNotFoundException(
                    "Não foi possível encontrar o cliente"
            );
        }

        return snapshot;
    }
}
