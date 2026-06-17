package com.mobile.pontoGestao.Dtos.Response;

import java.time.LocalDateTime;

public record ClienteResponse(
    String id,
    String nome,
    String telefone,
    String descricao,
    LocalDateTime dataCriacao
) {}