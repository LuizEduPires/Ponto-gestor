package com.mobile.pontoGestao.Dtos.Request;

import com.mobile.pontoGestao.Enums.TipoPedido;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.google.cloud.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public record ItemsPedidoRequest(
        @NotBlank(message = "Titulo não pode ser vazio ou nulo")
        String titulo,
        String descricao,
        @Min(value = 0, message = "Valor deve ser no minimo 0")
        @NotNull(message = "Deve existir um valor")
        Double valor,
        List<String> imagem,
        @NotNull(message = "É necessario uma data de prazo")
        Timestamp dataPrazo,
        Timestamp dataEntrega,
        Timestamp dataProva,
        @NotNull(message = "Item deve ter um tipo")
        TipoPedido tipo
        ) {
}
