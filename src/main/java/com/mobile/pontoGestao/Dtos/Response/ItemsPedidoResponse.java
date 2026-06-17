package com.mobile.pontoGestao.Dtos.Response;

import com.mobile.pontoGestao.Enums.TipoItemPedido;

import java.time.LocalDateTime;
import java.util.List;

public record ItemsPedidoResponse(
        String titulo,
        String descricao,
        Double valor,
        List<String> imagem,
        LocalDateTime dataPrazo,
        LocalDateTime dataEntrega,
        LocalDateTime dataProva,
        TipoItemPedido tipo
) {}