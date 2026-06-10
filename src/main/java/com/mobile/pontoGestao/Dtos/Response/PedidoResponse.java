package com.mobile.pontoGestao.Dtos.Response;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
import com.mobile.pontoGestao.Enums.TipoPedido;
import com.mobile.pontoGestao.Models.ItemsPedido;

import com.google.cloud.Timestamp;
import java.util.List;

public record PedidoResponse (
         String id,
         String titulo,
         List<ItemsPedido>itens,
         String idCliente,
         String nomeCliente,
         TipoPedido tipoPedido,
         StatusPedido statusPedido,
         String descricao,
         Timestamp dataProva,
         Timestamp dataEntrega,
         Timestamp dataPrazo,
         Double pagamentoAntecipado,
         Double saldo,
         TipoPagamento tipoPagamento){
}
