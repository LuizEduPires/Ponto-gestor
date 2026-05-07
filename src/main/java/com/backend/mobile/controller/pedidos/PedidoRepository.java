package com.costura.repository;

import com.costura.model.Pedido;
import com.costura.model.SituacaoPedido;
import com.costura.model.TipoPedido;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

 *   - salvar()         → cria um novo pedido
 *   - atualizar()      → atualiza campos de um pedido existente
 *   - buscarPorId()    → lê um único documento
 *   - listarPorDono()  → todos os pedidos de uma costureira
 *   - listarPorSituacao() → filtra por situação
 *   - listarPorTipo()     → filtra por tipo de serviço
 *   - listarPorCliente()  → histórico de um cliente
 *   - atualizarSituacao() → atualiza apenas a situação
 *   - excluir()        → remove o documento
 */
public class PedidoRepository {

    private static final String COLECAO = "pedidos";

    private final CollectionReference colecao;

    public PedidoRepository() {
        Firestore db = FirebaseConfig.getFirestore();
        this.colecao = db.collection(COLECAO);
    }

    // ════════════════════════════════════════════════════════════════
    //  CREATE
    // ════════════════════════════════════════════════════════════════

    public Pedido salvar(Pedido pedido) {
        try {
            DocumentReference docRef = colecao.document(); // gera ID automático
            pedido.setId(docRef.getId());

            ApiFuture<WriteResult> resultado = docRef.set(pedido.paraMap());
            resultado.get(); // aguarda a confirmação

            System.out.printf("[Firestore] Pedido salvo: %s%n", pedido.getId());
            return pedido;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao salvar pedido no Firestore.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  READ
    // ════════════════════════════════════════════════════════════════

    public Optional<Pedido> buscarPorId(String id) {
        try {
            DocumentSnapshot doc = colecao.document(id).get().get();

            if (!doc.exists()) {
                return Optional.empty();
            }

            return Optional.of(Pedido.deMap(doc.getId(), doc.getData()));

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao buscar pedido: " + id, e);
        }
    }

    public List<Pedido> listarPorDono(String uidDono) {
        try {
            Query query = colecao
                    .whereEqualTo("uidDono", uidDono)
                    .orderBy("criadoEm", Query.Direction.DESCENDING);

            return executarQuery(query);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao listar pedidos.", e);
        }
    }

    public List<Pedido> listarPorSituacao(String uidDono, SituacaoPedido situacao) {
        try {
            Query query = colecao
                    .whereEqualTo("uidDono", uidDono)
                    .whereEqualTo("situacao", situacao.name())
                    .orderBy("dataEntrega", Query.Direction.ASCENDING);

            return executarQuery(query);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao listar pedidos por situação.", e);
        }
    }

    public List<Pedido> listarPorTipo(String uidDono, TipoPedido tipo) {
        try {
            Query query = colecao
                    .whereEqualTo("uidDono", uidDono)
                    .whereEqualTo("tipo", tipo.name())
                    .orderBy("criadoEm", Query.Direction.DESCENDING);

            return executarQuery(query);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao listar pedidos por tipo.", e);
        }
    }

    public List<Pedido> listarPorCliente(String uidDono, String idCliente) {
        try {
            Query query = colecao
                    .whereEqualTo("uidDono", uidDono)
                    .whereEqualTo("idCliente", idCliente)
                    .orderBy("criadoEm", Query.Direction.DESCENDING);

            return executarQuery(query);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao listar pedidos do cliente.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UPDATE
    // ════════════════════════════════════════════════════════════════

    public Pedido atualizar(Pedido pedido) {
        if (pedido.getId() == null || pedido.getId().isBlank()) {
            throw new IllegalArgumentException("O ID do pedido é obrigatório para atualizar.");
        }

        try {
            Map<String, Object> dados = pedido.paraMap();
            dados.remove("criadoEm"); // não sobrescreve a data de criação original

            colecao.document(pedido.getId()).update(dados).get();

            System.out.printf("[Firestore] Pedido atualizado: %s%n", pedido.getId());
            return pedido;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao atualizar pedido: " + pedido.getId(), e);
        }
    }

    public void atualizarSituacao(String id, SituacaoPedido situacao) {
        try {
            colecao.document(id)
                   .update("situacao", situacao.name())
                   .get();

            System.out.printf("[Firestore] Situação do pedido %s → %s%n", id, situacao);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao atualizar situação do pedido: " + id, e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  DELETE
    // ════════════════════════════════════════════════════════════════


    public void excluir(String id) {
        try {
            colecao.document(id).delete().get();
            System.out.printf("[Firestore] Pedido excluído: %s%n", id);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao excluir pedido: " + id, e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  AUXILIAR
    // ════════════════════════════════════════════════════════════════

    private List<Pedido> executarQuery(Query query)
            throws InterruptedException, ExecutionException {

        ApiFuture<QuerySnapshot> futuro = query.get();
        QuerySnapshot snapshot = futuro.get();

        List<Pedido> pedidos = new ArrayList<>();
        snapshot.getDocuments().forEach(doc ->
                pedidos.add(Pedido.deMap(doc.getId(), doc.getData()))
        );

        return pedidos;
    }
}
