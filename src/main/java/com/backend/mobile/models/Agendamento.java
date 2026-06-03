package com.backend.mobile.models;

public class Agendamento {
    private String tipo;
    private String dataHora;
    private Boolean concluido;

    public Agendamento() {}

    public Agendamento(String tipo, String dataHora, Boolean concluido) {
        this.tipo = tipo;
        this.dataHora = dataHora;
        this.concluido = concluido;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public Boolean getConcluido() { return concluido; }
    public void setConcluido(Boolean concluido) { this.concluido = concluido; }
}