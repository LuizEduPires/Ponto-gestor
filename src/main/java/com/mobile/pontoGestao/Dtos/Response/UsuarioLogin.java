package com.mobile.pontoGestao.Dtos.Response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLogin(
        @Email(message = "Email não esta em um formato valido")
        @NotBlank(message = "Email não pode ser nulo ou vazio")
        String email,
        @NotBlank(message = "Senha não pode ser nula ou vazia")
        String senha) {
}
