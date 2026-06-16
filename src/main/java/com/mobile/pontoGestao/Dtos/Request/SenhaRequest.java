package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SenhaRequest(
        @NotBlank(message = "Senha não pode ser nula ou vazia")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
        )
        String senha) {
}
