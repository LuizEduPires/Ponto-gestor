package com.mobile.pontoGestao.Dtos.Response;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;

import java.util.List;

public record PedidoResponse(

        String id,
        String titulo,

        List<ItemsPedidoResponse> itens,

        String idCliente,
        String nomeCliente,

        StatusPedido statusPedido,

        Double pagamentoAntecipado,
        Double saldo,

        TipoPagamento tipoPagamento
) {}