package com.mobile.pontoGestao.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TipoItemPedido {
    CONFECCAO,
    REPARO,
    MODIFICACAO
}
