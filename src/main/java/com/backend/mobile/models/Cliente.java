package com.backend.mobile.models;

public class Cliente {

    private String id;
    private String nome;
    private String telefone;
    private String email;

    // Construtor vazio (Obrigatório para o Firebase funcionar)
    public Cliente() {
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}