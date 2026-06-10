package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "Nome não pode ser nulo ou vazio")
        String nome,
        @NotBlank(message = "Telefone não pode ser nulo ou vazio")
        String telefone, String descricao) {
}
