package com.mobile.pontoGestao.Dtos.Request;

import com.mobile.pontoGestao.Enums.TipoItemPedido;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record ItemsPedidoRequest(

        @NotBlank(message = "Titulo não pode ser vazio ou nulo")
        String titulo,

        String descricao,

        @NotNull(message = "Deve existir um valor")
        @Min(value = 0)
        Double valor,

        List<String> imagem,

        @NotNull(message = "É necessario uma data de prazo")
        LocalDateTime dataPrazo,

        LocalDateTime dataEntrega,

        LocalDateTime dataProva,

        @NotNull(message = "Item deve ter um tipo")
        TipoItemPedido tipo
) {}