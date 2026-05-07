package com.costura;

import com.costura.model.Pedido;
import com.costura.model.SituacaoPedido;
import com.costura.model.TipoPedido;
import com.costura.service.PedidoService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final String UID_DONO = "uid-costureira-exemplo-001";

    private static final PedidoService servico  = new PedidoService();
    private static final Scanner        scanner  = new Scanner(System.in);
    private static final SimpleDateFormat fmt    = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Sistema de Costura — Firebase      ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");

            try {
                rodando = processarOpcao(opcao);
            } catch (Exception e) {
                System.out.println("\n[ERRO] " + e.getMessage());
            }
        }

        System.out.println("\nSistema encerrado.");
        scanner.close();
    }

    // ════════════════════════════════════════════════════════════════

    private static void exibirMenu() {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("  1. Criar novo pedido");
        System.out.println("  2. Listar todos os pedidos");
        System.out.println("  3. Listar pedidos em produção");
        System.out.println("  4. Listar pedidos prontos");
        System.out.println("  5. Listar pedidos entregues");
        System.out.println("  6. Buscar pedido por ID");
        System.out.println("  7. Avançar situação de um pedido");
        System.out.println("  8. Excluir pedido");
        System.out.println("  9. Demonstração automática (dados de exemplo)");
        System.out.println("  0. Sair");
        System.out.println("──────────────────────────────────────");
    }

    private static boolean processarOpcao(int opcao) {
        return switch (opcao) {
            case 1  -> { criarPedidoInterativo();      yield true; }
            case 2  -> { listarPedidos(null);           yield true; }
            case 3  -> { listarPedidos(SituacaoPedido.EM_PRODUCAO); yield true; }
            case 4  -> { listarPedidos(SituacaoPedido.PRONTO);      yield true; }
            case 5  -> { listarPedidos(SituacaoPedido.ENTREGUE);    yield true; }
            case 6  -> { buscarPorId();                 yield true; }
            case 7  -> { avancarSituacao();             yield true; }
            case 8  -> { excluirPedido();               yield true; }
            case 9  -> { demonstracaoAutomatica();      yield true; }
            case 0  -> false;
            default -> { System.out.println("Opção inválida."); yield true; }
        };
    }

    // ════════════════════════════════════════════════════════════════
    //  AÇÕES DO MENU
    // ════════════════════════════════════════════════════════════════

    private static void criarPedidoInterativo() {
        System.out.println("\n── Novo Pedido ──");

        System.out.print("ID do cliente: ");
        String idCliente = scanner.nextLine().trim();

        System.out.print("Nome do cliente: ");
        String nomeCliente = scanner.nextLine().trim();

        System.out.print("Telefone do cliente: ");
        String telefoneCliente = scanner.nextLine().trim();

        System.out.println("Tipo (1=Reparo, 2=Confecção, 3=Modificação): ");
        int tipoNum = lerInteiro("");
        TipoPedido tipo = switch (tipoNum) {
            case 1 -> TipoPedido.REPARO;
            case 2 -> TipoPedido.CONFECCAO;
            case 3 -> TipoPedido.MODIFICACAO;
            default -> throw new IllegalArgumentException("Tipo inválido.");
        };

        int pecas = lerInteiro("Quantidade de peças: ");

        System.out.print("Descrição (opcional, Enter para pular): ");
        String descricao = scanner.nextLine().trim();

        System.out.print("Data de prova (dd/MM/aaaa, Enter para pular): ");
        Date dataProva = lerData(scanner.nextLine().trim());

        System.out.print("Data de entrega (dd/MM/aaaa): ");
        Date dataEntrega = lerData(scanner.nextLine().trim());

        double saldo = lerDouble("Saldo (R$): ");

        Pedido criado = servico.criarPedido(
                UID_DONO, idCliente, nomeCliente, telefoneCliente,
                tipo, pecas, descricao.isEmpty() ? null : descricao,
                dataProva, dataEntrega, saldo
        );

        System.out.println("\n✔ Pedido criado com sucesso!");
        imprimirPedido(criado);
    }

    private static void listarPedidos(SituacaoPedido filtroSituacao) {
        List<Pedido> pedidos = filtroSituacao == null
                ? servico.listarTodos(UID_DONO)
                : servico.listarPorSituacao(UID_DONO, filtroSituacao);    // chamada direta ao service

        if (pedidos.isEmpty()) {
            System.out.println("\nNenhum pedido encontrado.");
            return;
        }

        System.out.printf("\n── %d pedido(s) encontrado(s) ──%n", pedidos.size());
        pedidos.forEach(Main::imprimirPedido);
    }

    private static void buscarPorId() {
        System.out.print("ID do pedido: ");
        String id = scanner.nextLine().trim();

        Optional<Pedido> resultado = servico.buscarPorId(id);
        resultado.ifPresentOrElse(
            p -> { System.out.println("\n── Pedido encontrado ──"); imprimirPedido(p); },
            ()  -> System.out.println("Pedido não encontrado.")
        );
    }

    private static void avancarSituacao() {
        System.out.print("ID do pedido: ");
        String id = scanner.nextLine().trim();
        servico.avancarSituacao(id);
        System.out.println("✔ Situação avançada com sucesso!");
    }

    private static void excluirPedido() {
        System.out.print("ID do pedido: ");
        String id = scanner.nextLine().trim();
        System.out.print("Confirmar exclusão? (s/N): ");
        String confirmacao = scanner.nextLine().trim();

        if ("s".equalsIgnoreCase(confirmacao)) {
            servico.excluir(id);
            System.out.println("✔ Pedido excluído.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    private static void demonstracaoAutomatica() {
        System.out.println("\n── Demonstração automática ──");

        Date dataProva    = proximoDia(7);
        Date dataEntrega  = proximoDia(14);

        // 1. Cria pedido de reparo
        System.out.println("\n1. Criando pedido de reparo...");
        Pedido reparo = servico.criarPedido(
                UID_DONO, "cliente-gabriel-001",
                "Gabriel dos Reis Klein", "(48)996191767",
                TipoPedido.REPARO, 1,
                "Camisa social — ajuste no colarinho",
                dataProva, dataEntrega, 59.99
        );
        imprimirPedido(reparo);

        // 2. Cria pedido de confecção
        System.out.println("\n2. Criando pedido de confecção...");
        Pedido confeccao = servico.criarPedido(
                UID_DONO, "cliente-maria-002",
                "Maria Oliveira", "(48)991234567",
                TipoPedido.CONFECCAO, 2,
                "Vestido de festa — tecido fornecido pela cliente",
                dataProva, proximoDia(21), 350.00
        );
        imprimirPedido(confeccao);

        // 3. Avança situação do reparo: Em Produção → Pronto
        System.out.println("\n3. Avançando situação do reparo...");
        servico.avancarSituacao(reparo.getId());

        // 4. Lista todos os pedidos
        System.out.println("\n4. Listando todos os pedidos:");
        servico.listarTodos(UID_DONO).forEach(Main::imprimirPedido);

        // 5. Lista apenas em produção
        System.out.println("\n5. Listando apenas em produção:");
        servico.listarEmProducao(UID_DONO).forEach(Main::imprimirPedido);

        System.out.println("\n✔ Demonstração concluída!");
    }

    // ════════════════════════════════════════════════════════════════
    //  AUXILIARES
    // ════════════════════════════════════════════════════════════════

    private static List<Pedido> listarPorSituacao(String uidDono, SituacaoPedido situacao) {
        return switch (situacao) {
            case EM_PRODUCAO -> servico.listarEmProducao(uidDono);
            case PRONTO      -> servico.listarProntos(uidDono);
            case ENTREGUE    -> servico.listarEntregues(uidDono);
        };
    }

    private static void imprimirPedido(Pedido p) {
        System.out.println("  ┌─────────────────────────────────────");
        System.out.printf ("  │ ID:         %s%n", p.getId());
        System.out.printf ("  │ Cliente:    %s  %s%n", p.getNomeCliente(), p.getTelefoneCliente());
        System.out.printf ("  │ Tipo:       %s%n", p.getTipo());
        System.out.printf ("  │ Situação:   %s%n", p.getSituacao());
        System.out.printf ("  │ Peças:      %d%n", p.getQuantidadePecas());
        if (p.getDescricao() != null && !p.getDescricao().isBlank())
        System.out.printf ("  │ Descrição:  %s%n", p.getDescricao());
        if (p.getDataProva() != null)
        System.out.printf ("  │ Data prova: %s%n", fmt.format(p.getDataProva()));
        System.out.printf ("  │ Entrega:    %s%n",
                p.getDataEntrega() != null ? fmt.format(p.getDataEntrega()) : "—");
        System.out.printf ("  │ Saldo:      R$ %.2f%n", p.getSaldo());
        System.out.println("  └─────────────────────────────────────");
    }

    private static int lerInteiro(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String linha = scanner.nextLine().trim();
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Digite um número: ");
            }
        }
    }

    private static double lerDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Digite um número: ");
            }
        }
    }

    private static Date lerData(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return fmt.parse(texto);
        } catch (Exception e) {
            System.out.println("Data inválida, ignorada.");
            return null;
        }
    }

    private static Date proximoDia(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }
}
