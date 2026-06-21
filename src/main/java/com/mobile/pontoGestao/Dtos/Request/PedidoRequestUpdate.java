package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.Valid;

import java.util.List;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
public record PedidoRequestUpdate(
        String titulo,

        @Valid
        List<ItemsPedidoUpdateRequest> itens,

        String idCliente,
        Double pagamentoAntecipado,


        StatusPedido statusPedido,

        TipoPagamento tipoPagamento
) {}