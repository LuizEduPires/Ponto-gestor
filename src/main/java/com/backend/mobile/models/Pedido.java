package com.backend.mobile.models;

import java.util.List;

public class Pedido {
    private String clienteId;
    private String clienteNome;
    private String tipoServico;
    private String descricaoPeca;
    private Double valorTotal;
    private String id;
    private String status;

    private List<String> fotosAnexadas;
    private List<Agendamento> agendamentos;
    private List<Pagamento> pagamentos;

    public Pedido() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getTipoServico() { return tipoServico; }
    public void setTipoServico(String tipoServico) { this.tipoServico = tipoServico; }

    public String getDescricaoPeca() { return descricaoPeca; }
    public void setDescricaoPeca(String descricaoPeca) { this.descricaoPeca = descricaoPeca; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getFotosAnexadas() { return fotosAnexadas; }
    public void setFotosAnexadas(List<String> fotosAnexadas) { this.fotosAnexadas = fotosAnexadas; }

    public List<Agendamento> getAgendamentos() { return agendamentos; }
    public void setAgendamentos(List<Agendamento> agendamentos) { this.agendamentos = agendamentos; }

    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }
}