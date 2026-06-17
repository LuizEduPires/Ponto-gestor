package com.mobile.pontoGestao.Dtos.Request;

public record ClienteUpdateRequest(
        String nome,
        String telefone,
        String descricao
) {
}