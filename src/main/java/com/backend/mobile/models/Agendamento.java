package com.backend.mobile.models;

public class Agendamento {
    private String tipo; // Ex: "Prova", "Entrega"
    private String dataHora;
    private boolean concluido;

    public Agendamento() {}

    public Agendamento(String tipo, String dataHora, boolean concluido) {
        this.tipo = tipo;
        this.dataHora = dataHora;
        this.concluido = concluido;
    }


    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }
}