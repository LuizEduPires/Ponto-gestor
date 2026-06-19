package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.constraints.Email;

public record UsuarioUpdate(

        String nome,

        @Email(message = "Email não está em um formato válido")
        String email,

        String permissao
) {}