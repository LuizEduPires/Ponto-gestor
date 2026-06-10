package com.mobile.pontoGestao.Dtos.Response;

import com.google.cloud.Timestamp;

public record ClienteResponse(String id, String nome, String telefone, String descricao, Timestamp dataCriacao) {
}
