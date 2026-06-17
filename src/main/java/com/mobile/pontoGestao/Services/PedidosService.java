package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Request.SenhaRequest;
import com.mobile.pontoGestao.Dtos.Response.ItemsPedidoResponse;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoPedido;
import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;
import com.mobile.pontoGestao.Erros.LoginInvalidException;
import com.mobile.pontoGestao.Mappers.PedidosMapper;
import com.mobile.pontoGestao.Models.Clientes;
import com.mobile.pontoGestao.Models.ItemsPedido;
import com.mobile.pontoGestao.Models.Pedidos;
import com.mobile.pontoGestao.Models.Usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PedidosService {

    private final PedidosMapper pedidosMapper;
    private final Firestore firestore;
    private final PasswordEncoder passwordEncoder;

    public PedidoResponse criarPedido(PedidoRequest request)
            throws ExecutionException, InterruptedException {

        QuerySnapshot clienteSnapshot = getDocumentSnapshots(
                "clientes",
                request.idCliente(),
                "Não foi possível encontrar o cliente"
        );

        Clientes cliente = clienteSnapshot.getDocuments()
                .getFirst()
                .toObject(Clientes.class);

        Pedidos pedido = pedidosMapper.toModel(request);

        pedido.setNomeCliente(cliente.getNome());

        recalcularPedido(pedido);

        firestore.collection("pedidos")
                .document(pedido.getId())
                .set(pedido);

        return pedidosMapper.toDto(pedido);
    }

    public PedidoResponse verPedido(String id)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = getDocumentSnapshots(
                "pedidos",
                id,
                "Não foi possível encontrar o pedido"
        );

        Pedidos pedido = snapshot.getDocuments()
                .getFirst()
                .toObject(Pedidos.class);

        return pedidosMapper.toDto(pedido);
    }

    private Timestamp getMenorPrazo(PedidoResponse pedido) {

        if (pedido.itens() == null || pedido.itens().isEmpty()) {
            return null;
        }

        return pedido.itens()
                .stream()
                .map(ItemsPedidoResponse::dataPrazo)
                .filter(Objects::nonNull)
                .min(Timestamp::compareTo)
                .orElse(null);
    }

    public List<PedidoResponse> verPedidos(
            StatusPedido statusPedido,
            String titulo,
            OrdenacaoPedido ordenacao
    ) throws ExecutionException, InterruptedException {

        Query query = firestore.collection("pedidos");

        if (statusPedido != null) {
            query = query.whereEqualTo("statusPedido", statusPedido);
        }

        QuerySnapshot snapshot = query.get().get();

        Comparator<PedidoResponse> comparator = Comparator.comparing(PedidoResponse::titulo);

        if (ordenacao != null) {
            comparator = switch (ordenacao) {
                case NOME -> Comparator.comparing(PedidoResponse::nomeCliente);
                case TITULO -> Comparator.comparing(PedidoResponse::titulo);
                case PRAZO -> Comparator.comparing(
                        this::getMenorPrazo,
                        Comparator.nullsLast(Timestamp::compareTo)
                );
            };
        }

        return snapshot.getDocuments()
            .stream()
            .map(doc -> doc.toObject(Pedidos.class))
            .map(pedidosMapper::toDto)
            .filter(pedido ->
                    titulo == null ||
                    (
                        pedido.titulo() != null &&
                        pedido.titulo()
                                .toLowerCase()
                                .contains(titulo.toLowerCase())
                    )
            )
            .sorted(comparator)
            .toList();
    }

    public PedidoResponse atualizarPedido(
            String id,
            PedidoRequestUpdate request
    ) throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = getDocumentSnapshots(
                "pedidos",
                id,
                "Não foi possível encontrar o pedido"
        );

        Pedidos pedido = snapshot.getDocuments()
                .getFirst()
                .toObject(Pedidos.class);

        pedidosMapper.updateFromRequest(request, pedido);

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new RuntimeException(
                    "O pedido deve possuir ao menos um item"
            );
        }

        if (request.idCliente() != null) {

            QuerySnapshot clienteSnapshot = getDocumentSnapshots(
                    "clientes",
                    request.idCliente(),
                    "Não foi possível encontrar o cliente"
            );

            Clientes cliente = clienteSnapshot.getDocuments()
                    .getFirst()
                    .toObject(Clientes.class);

            pedido.setNomeCliente(cliente.getNome());
        }

        recalcularPedido(pedido);

        firestore.collection("pedidos")
                .document(pedido.getId())
                .set(pedido);

        return pedidosMapper.toDto(pedido);
    }

    public List<PedidoResponse> getPedidosByCliente(String idCliente)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = firestore.collection("pedidos")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .get();

        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Pedidos.class))
                .map(pedidosMapper::toDto)
                .toList();
    }

    public void deletarPedidos(
            String id,
            SenhaRequest request
    ) throws ExecutionException, InterruptedException {

        String idUsuario = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        QuerySnapshot snapshot = firestore.collection("usuarios")
                .whereEqualTo("id", idUsuario)
                .get()
                .get();

        if (snapshot.isEmpty()) {
            throw new EntityNotFoundException(
                    "Não foi possível encontrar o usuário"
            );
        }

        Usuarios usuario = snapshot.getDocuments()
                .getFirst()
                .toObject(Usuarios.class);

        if (!passwordEncoder.matches(
                request.senha(),
                usuario.getSenha()
        )) {
            throw new LoginInvalidException(
                    "Não foi possível validar a requisição"
            );
        }

        firestore.collection("pedidos")
                .document(id)
                .delete();
    }

    private void recalcularPedido(Pedidos pedido) {

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new RuntimeException("O pedido deve possuir ao menos um item");
        }

        double orcamento = pedido.getItens()
                .stream()
                .mapToDouble(ItemsPedido::getValor)
                .sum();

        double pagamentoAntecipado =
                pedido.getPagamentoAntecipado() == null
                        ? 0.0
                        : pedido.getPagamentoAntecipado();

        if (pagamentoAntecipado > orcamento) {
            throw new RuntimeException(
                    "Pagamento antecipado não pode ser maior que o orçamento"
            );
        }

        pedido.setOrcamento(orcamento);
        pedido.setQuantidade(pedido.getItens().size());
        pedido.setSaldo(orcamento - pagamentoAntecipado);
    }

    private QuerySnapshot getDocumentSnapshots(
            String colecao,
            String id,
            String message
    ) throws ExecutionException, InterruptedException {

        ApiFuture<QuerySnapshot> future = firestore.collection(colecao)
                .whereEqualTo("id", id)
                .get();

        QuerySnapshot snapshot = future.get();

        if (snapshot.isEmpty()) {
            throw new EntityNotFoundException(message);
        }

        return snapshot;
    }
}