package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Request.SenhaRequest;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PedidosService {

    private final PedidosMapper pedidosMapper;
    private final Firestore firestore;
    private final PasswordEncoder passwordEncoder;

    public PedidoResponse criarPedido(PedidoRequest request)
            throws ExecutionException, InterruptedException {

        Clientes cliente = getClienteById(request.idCliente());

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

        Pedidos pedido = getPedidoById(id);

        return pedidosMapper.toDto(pedido);
    }

    public List<PedidoResponse> verPedidos(
            StatusPedido statusPedido,
            String titulo,
            OrdenacaoPedido ordenacao
    ) throws ExecutionException, InterruptedException {

        var query = firestore.collection("pedidos");

        if (statusPedido != null) {
            query = query.whereEqualTo("statusPedido", statusPedido);
        }

        QuerySnapshot snapshot = query.get().get();

        Comparator<PedidoResponse> comparator = switch (ordenacao) {
            case NOME -> Comparator.comparing(PedidoResponse::nomeCliente);
            case TITULO -> Comparator.comparing(PedidoResponse::titulo);
            case PRAZO -> Comparator.comparing(
                    PedidoResponse::dataPrazo,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case null -> Comparator.comparing(PedidoResponse::titulo);
        };

        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Pedidos.class))
                .map(pedidosMapper::toDto)
                .filter(p ->
                        titulo == null ||
                        p.titulo().toLowerCase().contains(titulo.toLowerCase())
                )
                .sorted(comparator)
                .toList();
    }

    public PedidoResponse atualizarPedido(
            String id,
            PedidoRequestUpdate request
    ) throws ExecutionException, InterruptedException {

        Pedidos pedido = getPedidoById(id);

        pedidosMapper.updateFromRequest(request, pedido);

        if (request.idCliente() != null) {
            Clientes cliente = getClienteById(request.idCliente());
            pedido.setNomeCliente(cliente.getNome());
        }

        recalcularPedido(pedido);

        firestore.collection("pedidos")
                .document(id)
                .set(pedido);

        return pedidosMapper.toDto(pedido);
    }

    public void deletarPedidos(String id, SenhaRequest request)
            throws ExecutionException, InterruptedException {

        String idUsuario = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        Usuarios usuario = getUsuarioById(idUsuario);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new LoginInvalidException("Senha inválida");
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

        double pagamentoAntecipado = Objects.requireNonNullElse(
                pedido.getPagamentoAntecipado(),
                0.0
        );

        if (pagamentoAntecipado > orcamento) {
            throw new RuntimeException(
                    "Pagamento antecipado não pode ser maior que o orçamento"
            );
        }

        pedido.setOrcamento(orcamento);
        pedido.setQuantidade(pedido.getItens().size());
        pedido.setSaldo(orcamento - pagamentoAntecipado);
    }


    private Pedidos getPedidoById(String id)
            throws ExecutionException, InterruptedException {

        var doc = firestore.collection("pedidos")
                .document(id)
                .get()
                .get();

        if (!doc.exists()) {
            throw new EntityNotFoundException("Pedido não encontrado");
        }

        return doc.toObject(Pedidos.class);
    }

    private Clientes getClienteById(String id)
            throws ExecutionException, InterruptedException {

        var snapshot = firestore.collection("clientes")
                .whereEqualTo("id", id)
                .get()
                .get();

        if (snapshot.isEmpty()) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }

        return snapshot.getDocuments()
                .getFirst()
                .toObject(Clientes.class);
    }

    private Usuarios getUsuarioById(String id)
            throws ExecutionException, InterruptedException {

        var snapshot = firestore.collection("usuarios")
                .whereEqualTo("id", id)
                .get()
                .get();

        if (snapshot.isEmpty()) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return snapshot.getDocuments()
                .getFirst()
                .toObject(Usuarios.class);
    }
}