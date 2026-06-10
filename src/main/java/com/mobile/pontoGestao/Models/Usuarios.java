package com.mobile.pontoGestao.Models;

import lombok.Data;

import java.util.UUID;

@Data
public class Usuarios {
    private String id = UUID.randomUUID().toString();
    private String nome;
    private String email;
    private String senha;
}
