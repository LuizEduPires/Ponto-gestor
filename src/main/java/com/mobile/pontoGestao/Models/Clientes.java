package com.mobile.pontoGestao.Models;

import lombok.Data;

import java.util.UUID;
import com.google.cloud.Timestamp;

@Data
public class Clientes {
    private String id = UUID.randomUUID().toString();
    private String nome;
    private String telefone;
    private String descricao = "";
    private Timestamp  dataCriacao = Timestamp.now();
}
