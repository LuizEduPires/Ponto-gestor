package com.mobile.pontoGestao.Dtos.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import com.mobile.pontoGestao.Enums.TipoPagamento;
import java.util.List;

public record PedidoRequest(

        @NotBlank(message = "Título não pode ser vazio ou nulo")
        String titulo,

        @Valid
        @NotEmpty(message = "Um pedido deve ter ao menos um item")
        List<ItemsPedidoRequest> itens,

        @NotBlank(message = "O id do cliente não pode ser nulo ou vazio")
        String idCliente,

        @NotNull(message = "Pagamento antecipado não pode ser nulo")
        @PositiveOrZero(message = "Pagamento antecipado não pode ser negativo")
        Double pagamentoAntecipado,

        @NotNull(message = "Tipo de pagamento é obrigatório")
        TipoPagamento tipoPagamento
) {}