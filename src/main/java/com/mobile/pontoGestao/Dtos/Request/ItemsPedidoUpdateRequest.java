package com.mobile.pontoGestao.Dtos.Request;

import java.util.List;
import com.google.cloud.Timestamp;
import com.mobile.pontoGestao.Enums.TipoItemPedido;

public record ItemsPedidoUpdateRequest(
        String titulo,
        String descricao,
        Double valor,
        List<String> imagem,
        Timestamp dataPrazo,
        Timestamp dataEntrega,
        Timestamp dataProva,
        TipoItemPedido tipo
) {}
