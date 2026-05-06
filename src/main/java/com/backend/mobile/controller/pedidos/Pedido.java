package com.costura.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa um pedido de costura.
 *
 * Campos idênticos à coleção "pedidos" no Firestore:
 *   id, uidDono, idCliente, nomeCliente, telefoneCliente,
 *   tipo, situacao, quantidadePecas, descricao,
 *   dataProva, dataEntrega, saldo, modelosCliente, criadoEm
 */
public class Pedido {

    // ── identificadores ────────────────────────────────────────────
    private String id;              // gerado automaticamente pelo Firestore
    private String uidDono;         // FK → usuarios
    private String idCliente;       // FK → clientes
    private String nomeCliente;     // desnormalizado
    private String telefoneCliente; // desnormalizado

    // ── tipo e situação ─────────────────────────────────────────────
    private TipoPedido tipo;
    private SituacaoPedido situacao;

    // ── detalhes do serviço ─────────────────────────────────────────
    private int quantidadePecas;
    private String descricao;

    // ── datas ───────────────────────────────────────────────────────
    private Date dataProva;
    private Date dataEntrega;
    private Date criadoEm;

    // ── financeiro ──────────────────────────────────────────────────
    private double saldo;

    // ── referências de imagens ──────────────────────────────────────
    private List<String> modelosCliente;

    // ── construtores ────────────────────────────────────────────────

    public Pedido() {
        this.modelosCliente = new ArrayList<>();
        this.situacao = SituacaoPedido.EM_PRODUCAO;
        this.criadoEm = new Date();
    }

    public Pedido(String uidDono, String idCliente, String nomeCliente,
                  String telefoneCliente, TipoPedido tipo,
                  int quantidadePecas, Date dataProva, Date dataEntrega,
                  double saldo) {
        this();
        this.uidDono         = uidDono;
        this.idCliente       = idCliente;
        this.nomeCliente     = nomeCliente;
        this.telefoneCliente = telefoneCliente;
        this.tipo            = tipo;
        this.quantidadePecas = quantidadePecas;
        this.dataProva       = dataProva;
        this.dataEntrega     = dataEntrega;
        this.saldo           = saldo;
    }

    // ── conversão para/de Firestore ─────────────────────────────────

    public Map<String, Object> paraMap() {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("uidDono",          uidDono);
        mapa.put("idCliente",        idCliente);
        mapa.put("nomeCliente",      nomeCliente);
        mapa.put("telefoneCliente",  telefoneCliente);
        mapa.put("tipo",             tipo != null ? tipo.name() : null);
        mapa.put("situacao",         situacao != null ? situacao.name() : null);
        mapa.put("quantidadePecas",  quantidadePecas);
        mapa.put("descricao",        descricao);
        mapa.put("dataProva",        dataProva);
        mapa.put("dataEntrega",      dataEntrega);
        mapa.put("saldo",            saldo);
        mapa.put("modelosCliente",   modelosCliente);
        mapa.put("criadoEm",         criadoEm);
        return mapa;
    }

    /**
     * Cria um Pedido a partir de um Map recuperado do Firestore.
     */
    public static Pedido deMap(String id, Map<String, Object> dados) {
        Pedido p = new Pedido();
        p.id              = id;
        p.uidDono         = (String) dados.get("uidDono");
        p.idCliente       = (String) dados.get("idCliente");
        p.nomeCliente     = (String) dados.get("nomeCliente");
        p.telefoneCliente = (String) dados.get("telefoneCliente");
        p.descricao       = (String) dados.get("descricao");

        if (dados.get("tipo") != null)
            p.tipo = TipoPedido.fromString((String) dados.get("tipo"));

        if (dados.get("situacao") != null)
            p.situacao = SituacaoPedido.fromString((String) dados.get("situacao"));

        if (dados.get("quantidadePecas") instanceof Number n)
            p.quantidadePecas = n.intValue();

        if (dados.get("saldo") instanceof Number n)
            p.saldo = n.doubleValue();

        if (dados.get("dataProva") instanceof com.google.cloud.Timestamp ts)
            p.dataProva = ts.toDate();

        if (dados.get("dataEntrega") instanceof com.google.cloud.Timestamp ts)
            p.dataEntrega = ts.toDate();

        if (dados.get("criadoEm") instanceof com.google.cloud.Timestamp ts)
            p.criadoEm = ts.toDate();

        @SuppressWarnings("unchecked")
        List<String> modelos = (List<String>) dados.get("modelosCliente");
        if (modelos != null) p.modelosCliente = modelos;

        return p;
    }

    // ── toString ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "Pedido{id='%s', cliente='%s', tipo=%s, situacao=%s, pecas=%d, saldo=R$%.2f}",
            id, nomeCliente, tipo, situacao, quantidadePecas, saldo
        );
    }

    // ── getters e setters ────────────────────────────────────────────

    public String getId()                      { return id; }
    public void setId(String id)               { this.id = id; }

    public String getUidDono()                 { return uidDono; }
    public void setUidDono(String uidDono)     { this.uidDono = uidDono; }

    public String getIdCliente()               { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getNomeCliente()             { return nomeCliente; }
    public void setNomeCliente(String n)       { this.nomeCliente = n; }

    public String getTelefoneCliente()         { return telefoneCliente; }
    public void setTelefoneCliente(String t)   { this.telefoneCliente = t; }

    public TipoPedido getTipo()                { return tipo; }
    public void setTipo(TipoPedido tipo)       { this.tipo = tipo; }

    public SituacaoPedido getSituacao()        { return situacao; }
    public void setSituacao(SituacaoPedido s)  { this.situacao = s; }

    public int getQuantidadePecas()            { return quantidadePecas; }
    public void setQuantidadePecas(int q)      { this.quantidadePecas = q; }

    public String getDescricao()               { return descricao; }
    public void setDescricao(String d)         { this.descricao = d; }

    public Date getDataProva()                 { return dataProva; }
    public void setDataProva(Date d)           { this.dataProva = d; }

    public Date getDataEntrega()               { return dataEntrega; }
    public void setDataEntrega(Date d)         { this.dataEntrega = d; }

    public double getSaldo()                   { return saldo; }
    public void setSaldo(double saldo)         { this.saldo = saldo; }

    public List<String> getModelosCliente()    { return modelosCliente; }
    public void setModelosCliente(List<String> m) { this.modelosCliente = m; }

    public Date getCriadoEm()                  { return criadoEm; }
    public void setCriadoEm(Date d)            { this.criadoEm = d; }
}
