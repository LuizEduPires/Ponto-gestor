package com.costura.service;

import com.costura.model.Pedido;
import com.costura.model.SituacaoPedido;
import com.costura.model.TipoPedido;
import com.costura.repository.PedidoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PedidoService {

    private final PedidoRepository repositorio;

    public PedidoService() {
        this.repositorio = new PedidoRepository();
    }

    // ── injeção de dependência (facilita testes unitários) ──────────
    public PedidoService(PedidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // ════════════════════════════════════════════════════════════════
    //  CRIAR PEDIDO
    // ════════════════════════════════════════════════════════════════

    public Pedido criarPedido(String uidDono, String idCliente, String nomeCliente,
                              String telefoneCliente, TipoPedido tipo,
                              int quantidadePecas, String descricao,
                              Date dataProva, Date dataEntrega, double saldo) {

        validarCamposObrigatorios(uidDono, idCliente, nomeCliente,
                                  tipo, quantidadePecas, dataEntrega, saldo);

        if (dataEntrega.before(new Date())) {
            throw new IllegalArgumentException("A data de entrega não pode ser no passado.");
        }

        if (dataProva != null && dataProva.after(dataEntrega)) {
            throw new IllegalArgumentException("A data de prova deve ser anterior à data de entrega.");
        }

        Pedido pedido = new Pedido(uidDono, idCliente, nomeCliente,
                                   telefoneCliente, tipo,
                                   quantidadePecas, dataProva, dataEntrega, saldo);
        pedido.setDescricao(descricao);

        return repositorio.salvar(pedido);
    }

    // ════════════════════════════════════════════════════════════════
    //  CONSULTAS
    // ════════════════════════════════════════════════════════════════

    public Optional<Pedido> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O ID do pedido é obrigatório.");
        }
        return repositorio.buscarPorId(id);
    }

    public List<Pedido> listarTodos(String uidDono) {
        validarUid(uidDono);
        return repositorio.listarPorDono(uidDono);
    }

    public List<Pedido> listarEmProducao(String uidDono) {
        validarUid(uidDono);
        return repositorio.listarPorSituacao(uidDono, SituacaoPedido.EM_PRODUCAO);
    }

    public List<Pedido> listarProntos(String uidDono) {
        validarUid(uidDono);
        return repositorio.listarPorSituacao(uidDono, SituacaoPedido.PRONTO);
    }

    public List<Pedido> listarEntregues(String uidDono) {
        validarUid(uidDono);
        return repositorio.listarPorSituacao(uidDono, SituacaoPedido.ENTREGUE);
    }

    public List<Pedido> listarPorTipo(String uidDono, TipoPedido tipo) {
        validarUid(uidDono);
        if (tipo == null) throw new IllegalArgumentException("O tipo é obrigatório.");
        return repositorio.listarPorTipo(uidDono, tipo);
    }

    public List<Pedido> listarPorCliente(String uidDono, String idCliente) {
        validarUid(uidDono);
        if (idCliente == null || idCliente.isBlank())
            throw new IllegalArgumentException("O ID do cliente é obrigatório.");
        return repositorio.listarPorCliente(uidDono, idCliente);
    }

    // ════════════════════════════════════════════════════════════════
    //  ATUALIZAR
    // ════════════════════════════════════════════════════════════════

    public void avancarSituacao(String id) {
        Pedido pedido = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        SituacaoPedido novaSituacao = switch (pedido.getSituacao()) {
            case EM_PRODUCAO -> SituacaoPedido.PRONTO;
            case PRONTO      -> SituacaoPedido.ENTREGUE;
            case ENTREGUE    -> throw new IllegalStateException(
                    "Pedido já foi entregue. Não é possível avançar mais.");
        };

        repositorio.atualizarSituacao(id, novaSituacao);
        System.out.printf("Pedido %s: %s → %s%n", id, pedido.getSituacao(), novaSituacao);
    }

    public Pedido atualizar(Pedido pedido) {
        if (pedido.getId() == null || pedido.getId().isBlank())
            throw new IllegalArgumentException("O ID do pedido é obrigatório para atualizar.");
        return repositorio.atualizar(pedido);
    }

    // ════════════════════════════════════════════════════════════════
    //  EXCLUIR
    // ════════════════════════════════════════════════════════════════

    public void excluir(String id) {
        Pedido pedido = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if (pedido.getSituacao() == SituacaoPedido.ENTREGUE) {
            throw new IllegalStateException(
                "Pedido já entregue não pode ser excluído. " +
                "Considere mantê-lo como histórico."
            );
        }

        repositorio.excluir(id);
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDAÇÕES PRIVADAS
    // ════════════════════════════════════════════════════════════════

    private void validarUid(String uidDono) {
        if (uidDono == null || uidDono.isBlank())
            throw new IllegalArgumentException("O UID do usuário é obrigatório.");
    }

    private void validarCamposObrigatorios(String uidDono, String idCliente,
                                            String nomeCliente, TipoPedido tipo,
                                            int quantidadePecas, Date dataEntrega,
                                            double saldo) {
        validarUid(uidDono);

        if (idCliente == null || idCliente.isBlank())
            throw new IllegalArgumentException("O cliente é obrigatório.");

        if (nomeCliente == null || nomeCliente.isBlank())
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");

        if (tipo == null)
            throw new IllegalArgumentException("O tipo de costura é obrigatório.");

        if (quantidadePecas <= 0)
            throw new IllegalArgumentException("A quantidade de peças deve ser maior que zero.");

        if (dataEntrega == null)
            throw new IllegalArgumentException("A data de entrega é obrigatória.");

        if (saldo < 0)
            throw new IllegalArgumentException("O saldo não pode ser negativo.");
    }
}
