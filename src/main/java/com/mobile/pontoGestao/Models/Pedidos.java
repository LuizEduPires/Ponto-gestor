package com.mobile.pontoGestao.Models;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
import com.mobile.pontoGestao.Enums.TipoPedido;
import lombok.Data;
import com.google.cloud.Timestamp;

import java.util.List;
import java.util.UUID;

@Data
public class Pedidos {
    private String id = UUID.randomUUID().toString();
    private String titulo;
    private List<ItemsPedido> itens;
    private String idCliente;
    private String nomeCliente;
    private TipoPedido tipoPedido;
    private StatusPedido statusPedido = StatusPedido.PRODUCAO;
    private String descricao;
    private Timestamp dataProva;
    private Timestamp dataEntrega;
    private Timestamp dataPrazo;
    private Double pagamentoAntecipado;
    private Double orcamento;
    private Double saldo;
    private Integer quantidade;
    private TipoPagamento tipoPagamento;

}
