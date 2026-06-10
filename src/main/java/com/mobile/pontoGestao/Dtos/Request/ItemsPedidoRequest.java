package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemsPedidoRequest(
        @NotBlank(message = "Titulo não pode ser vazio ou nulo")
        String titulo,
        String descricao,
        @Min(value = 0, message = "Valor deve ser no minimo 0")
        @NotNull(message = "Deve existir um valor")
        Double valor,
        String imagem) {
}
