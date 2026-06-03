package com.backend.mobile.models;

public class Pagamento {
    private String id;
    private String tipoPagamento;
    private Double valor;
    private String dataPagamento;

    public Pagamento() {}

    public Pagamento(String tipoPagamento, Double valor, String dataPagamento) {
        this.tipoPagamento = tipoPagamento;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(String dataPagamento) { this.dataPagamento = dataPagamento; }
}