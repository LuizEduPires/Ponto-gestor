package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Enums.OrdenacaoPedido;
import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPedido;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;
import com.mobile.pontoGestao.Mappers.ItemsPedidoMapper;
import com.mobile.pontoGestao.Mappers.PedidosMapper;
import com.mobile.pontoGestao.Models.Clientes;
import com.mobile.pontoGestao.Models.ItemsPedido;
import com.mobile.pontoGestao.Models.Pedidos;
import lombok.RequiredArgsConstructor;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PedidosService {
    private final ItemsPedidoMapper itemsPedidoMapper;
    private final PedidosMapper pedidosMapper;
    private final Firestore firestore;

    public PedidoResponse criarPedido(PedidoRequest request) throws ExecutionException, InterruptedException {
        List<ItemsPedido> itemsPedidos = request.itens().stream().map(itemsPedidoMapper::toModel).toList();
        QuerySnapshot query = getDocumentSnapshots("clientes", request.idCliente(), "Não foi possivel encontrar o cliente pedido");
        Clientes cliente = query.getDocuments().getFirst().toObject(Clientes.class);
        Pedidos pedido = pedidosMapper.toModel(request);

        pedido.setItens(itemsPedidos);
        Double orcamento = pedido.getItens().stream().mapToDouble(ItemsPedido::getValor).sum();
        Integer quantidade = pedido.getItens().size();
        Double saldo = orcamento - pedido.getPagamentoAntecipado();

        pedido.setSaldo(saldo);
        pedido.setQuantidade(quantidade);
        pedido.setOrcamento(orcamento);
        pedido.setNomeCliente(cliente.getNome());

        if (request.dataProva() != null) {
            pedido.setDataProva(convertTimestamp(request.dataProva()));
        }

        if (request.dataEntrega() != null) {
            pedido.setDataEntrega(convertTimestamp(request.dataEntrega()));
        }

        pedido.setDataPrazo(convertTimestamp(request.dataPrazo()));
        firestore.collection("pedidos").document(pedido.getId()).set(pedido);
        return pedidosMapper.toDto(pedido);
    }

    public PedidoResponse verPedido(String id) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = getDocumentSnapshots("pedidos", id, "Não foi possivel encontrar o  pedido");

        Pedidos pedido = snapshot.getDocuments().getFirst().toObject(Pedidos.class);
        return pedidosMapper.toDto(pedido);
    }

    public List<PedidoResponse> verPedidos(StatusPedido statusPedido, TipoPedido tipoPedido, String titulo, OrdenacaoPedido ordenacao) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future =  firestore.collection("pedidos")
                .get();

        QuerySnapshot snapshot = future.get();
        Comparator<PedidoResponse> comparator = Comparator.comparing(PedidoResponse::dataPrazo);

        if (ordenacao != null)
        {
            comparator = switch (ordenacao) {
                case  NOME-> Comparator.comparing(PedidoResponse::nomeCliente);
                case TITULO -> Comparator.comparing(PedidoResponse::titulo);
                default -> Comparator.comparing(PedidoResponse::dataPrazo);
            };
        }

        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Pedidos.class))
                .map(pedidosMapper::toDto)
                .filter(pedido -> statusPedido == null || statusPedido.equals(pedido.statusPedido()))
                .filter(pedido -> tipoPedido == null || tipoPedido.equals(pedido.tipoPedido()))
                .filter(pedido -> titulo == null || pedido.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .sorted(comparator)
                .toList();
    }

    public PedidoResponse atualizarPedido(String id, PedidoRequestUpdate request) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = getDocumentSnapshots("pedidos", id, "Não foi possivel encontrar o  pedido");

        Pedidos pedido = snapshot.getDocuments().getFirst().toObject(Pedidos.class);

        if (request.itens() != null) {
            //TODO: inventar um erro pra isso
            if (request.itens().isEmpty()) throw new RuntimeException("Nenhum item foi encontrado");
            Integer quantidade = pedido.getItens().size();

            Double orcamento =  pedido.getItens().stream().mapToDouble(ItemsPedido::getValor).sum();

            if (request.pagamentoAntecipado() != null) {
                Double saldo = orcamento - request.pagamentoAntecipado();
                pedido.setSaldo(saldo);
                pedido.setQuantidade(quantidade);
                pedido.setOrcamento(orcamento);
            } else {
                Double saldo = orcamento - pedido.getPagamentoAntecipado();
                pedido.setSaldo(saldo);
                pedido.setQuantidade(quantidade);
                pedido.setOrcamento(orcamento);
            }
        }

        if (request.dataProva() != null) {
            pedido.setDataProva(convertTimestamp(request.dataProva()));
        }

        if (request.dataEntrega() != null) {
            pedido.setDataEntrega(convertTimestamp(request.dataEntrega()));
        }

        if (request.dataPrazo() != null) {
            pedido.setDataPrazo(convertTimestamp(request.dataPrazo()));
        }

        pedidosMapper.updatePedido(request, pedido);
        firestore.collection("pedidos").document(pedido.getId()).set(pedido);
        return pedidosMapper.toDto(pedido);
    }

    private QuerySnapshot getDocumentSnapshots(String colecao, String id, String message) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = firestore.collection(colecao)
                .whereEqualTo("id", id)
                .get();

        QuerySnapshot snapshot = future.get();
        if (snapshot.isEmpty()) throw new EntityNotFoundException(message);
        return snapshot;
    }

    public void deletarPedidos(String id) {
        firestore.collection("pedidos").document(id).delete();
    }

    private Timestamp convertTimestamp(LocalDateTime horario) {
        return Timestamp.of(
                Date.from(
                        horario
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }
}
