package com.mobile.pontoGestao.Dtos.Response;

import com.mobile.pontoGestao.Enums.TipoItemPedido;
import com.google.cloud.Timestamp;
import java.util.List;

public record ItemsPedidoResponse(
        String titulo,
        String descricao,
        Double valor,
        List<String> imagem,
        Timestamp dataEntrega,
        Timestamp dataProva,
        Timestamp dataPrazo,
        TipoItemPedido tipo
) {}