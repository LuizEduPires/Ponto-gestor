package com.mobile.pontoGestao.Dtos.Request;

import java.time.LocalDateTime;
import java.util.List;
import com.mobile.pontoGestao.Enums.TipoItemPedido;

public record ItemsPedidoUpdateRequest(

    String titulo,
    String descricao,
    Double valor,
    List<String> imagem,

    LocalDateTime dataPrazo,
    LocalDateTime dataEntrega,
    LocalDateTime dataProva,

    TipoItemPedido tipo
) {}
