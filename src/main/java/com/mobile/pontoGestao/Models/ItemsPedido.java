package com.mobile.pontoGestao.Models;

import com.mobile.pontoGestao.Enums.TipoPedido;
import lombok.Data;

import com.google.cloud.Timestamp;
import java.util.List;

@Data
public class ItemsPedido {
    private String titulo;
    private String descricao;
    private Double valor;
    private List<String> imagem;
    private Timestamp dataEntrega;
    private Timestamp dataProva;
    private Timestamp dataPrazo;
    private TipoPedido tipo;
}
