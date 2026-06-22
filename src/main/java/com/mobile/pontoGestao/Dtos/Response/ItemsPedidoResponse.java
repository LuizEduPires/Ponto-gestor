package com.mobile.pontoGestao.Dtos.Response;

import com.mobile.pontoGestao.Enums.StatusItemPedido;
import com.mobile.pontoGestao.Enums.TipoItemPedido;

import java.time.LocalDateTime;
import java.util.List;

public record ItemsPedidoResponse(
        String id,
        String titulo,
        String descricao,
        Double valor,
        List<String> imagem,
        LocalDateTime dataPrazo,
        LocalDateTime dataEntrega,
        LocalDateTime dataProva,
        StatusItemPedido statusItemPedido,
        TipoItemPedido tipo
) {}