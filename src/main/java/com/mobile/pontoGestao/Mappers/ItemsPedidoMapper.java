package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {

    @Mapping(target = "dataPrazo", source = "dataPrazo")
    @Mapping(target = "dataEntrega", source = "dataEntrega")
    @Mapping(target = "dataProva", source = "dataProva")
    ItemsPedido toModel(ItemsPedidoRequest request);

    default com.google.cloud.Timestamp map(java.time.LocalDateTime value) {
        if (value == null) return null;

        return com.google.cloud.Timestamp.of(
                java.util.Date.from(
                        value.atZone(java.time.ZoneId.systemDefault()).toInstant()
                )
        );
    }
}