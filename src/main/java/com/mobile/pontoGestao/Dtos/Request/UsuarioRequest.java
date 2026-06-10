package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UsuarioRequest(
        @NotBlank(message = "Nome não pode ser nulo ou vazio")
        String nome,
        @Email(message = "Email não esta em um formato valido")
        @NotBlank(message = "Email não pode ser nulo ou vazio")
        String email,
        @NotBlank(message = "Senha não pode ser nula ou vazia")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
        )
        String senha) {
}
