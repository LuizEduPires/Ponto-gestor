package com.mobile.pontoGestao.Dtos.Request;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
import com.mobile.pontoGestao.Enums.TipoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoRequestUpdate(
        String titulo,
        List<ItemsPedidoRequest> itens,
        String idCliente,
        TipoPedido tipoPedido,
        String descricao,
        LocalDateTime dataProva,
        LocalDateTime dataEntrega,
        LocalDateTime dataPrazo,
        Double pagamentoAntecipado,
        TipoPagamento tipoPagamento,
        StatusPedido statusPedido) {
}
