package com.mobile.pontoGestao.Models;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class Pedidos {
    private String id = UUID.randomUUID().toString();
    private String titulo;
    private List<ItemsPedido> itens;
    private String idCliente;
    private String nomeCliente;
    private StatusPedido statusPedido = StatusPedido.PENDENTE;
    private Double pagamentoAntecipado;
    private Double orcamento;
    private Double saldo;
    private Integer quantidade;
    private TipoPagamento tipoPagamento;

}
