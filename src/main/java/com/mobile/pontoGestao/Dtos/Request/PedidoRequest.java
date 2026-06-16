package com.mobile.pontoGestao.Dtos.Request;

import com.mobile.pontoGestao.Enums.StatusPedido;
import com.mobile.pontoGestao.Enums.TipoPagamento;
import com.mobile.pontoGestao.Enums.TipoPedido;
import com.mobile.pontoGestao.Models.ItemsPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoRequest(
        @NotBlank(message = "Titulo não pode ser vazio ou nulo")
        String titulo,
        @Valid
        @NotEmpty(message = "Um pedido deve ter ao menos um item")
        List<ItemsPedidoRequest> itens,
        @NotBlank(message = "O id do cliente não pode ser nulo ou vazio")
        String idCliente,
        @Min(value = 0, message = "Pagamento minimo deve ser 0")
        Double pagamentoAntecipado,
        @NotNull(message = "O tipo do pagamento é obrigatorio")
        TipoPagamento tipoPagamento) {
}
