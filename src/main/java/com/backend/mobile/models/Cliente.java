package com.backend.mobile.models;

public class Cliente {
    private String nome;
    private String telefone;

    public Cliente() {}

    public Cliente(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}