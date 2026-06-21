package com.mobile.pontoGestao.Services;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Response.ItemsPedidoResponse;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;

import com.mobile.pontoGestao.Enums.OrdenacaoPedido;
import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;

import com.mobile.pontoGestao.Mappers.PedidosMapper;
import com.mobile.pontoGestao.Models.Clientes;
import com.mobile.pontoGestao.Models.ItemsPedido;
import com.mobile.pontoGestao.Models.Pedidos;
import com.mobile.pontoGestao.Models.Usuarios;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        return pedidosMapper.toDto(getPedidoById(id));
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

        if (ordenacao == null) {
            ordenacao = OrdenacaoPedido.TITULO;
        }
      
        Comparator<PedidoResponse> comparator = switch (ordenacao) {
            case NOME -> Comparator.comparing(PedidoResponse::nomeCliente);
            case TITULO -> Comparator.comparing(PedidoResponse::titulo);
            case PRAZO -> Comparator.comparing(
                    p -> p.itens().stream()
                            .map(ItemsPedidoResponse::dataPrazo)
                            .filter(Objects::nonNull)
                            .min(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MAX)
            );
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

    public PedidoResponse atualizarPedido(String id, PedidoRequestUpdate request) 
        throws ExecutionException, InterruptedException {

        Pedidos pedido = getPedidoById(id);
        List<ItemsPedido> itensAntigos = pedido.getItens();

        pedidosMapper.updateFromRequest(request, pedido);

        if (request.itens() == null) { 
            pedido.setItens(itensAntigos);
        } else {
            preservarImagensDosItens(pedido.getItens(), itensAntigos);
        }

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

    public void deletarPedidos(String id)
            throws ExecutionException, InterruptedException {

        getPedidoById(id); 

        firestore.collection("pedidos")
                .document(id)
                .delete();
    }

    private void recalcularPedido(Pedidos pedido) {

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve possuir ao menos um item"
            );
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
            throw new IllegalArgumentException(
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

    private void preservarImagensDosItens(List<ItemsPedido> itensNovos, List<ItemsPedido> itensAntigos) {
        if (itensAntigos == null || itensNovos == null) return;

        for (ItemsPedido itemNovo : itensNovos) {
            if (itemNovo.getImagem() != null && !itemNovo.getImagem().isEmpty()) {
                continue;
            }

            itensAntigos.stream()
                .filter(antigo -> antigo.getTitulo() != null && antigo.getTitulo().equalsIgnoreCase(itemNovo.getTitulo()))
                .filter(antigo -> antigo.getTipo() == itemNovo.getTipo())
                .findFirst()
                .ifPresent(antigo -> {
                    itemNovo.setImagem(antigo.getImagem());
                });
        }
    }
}