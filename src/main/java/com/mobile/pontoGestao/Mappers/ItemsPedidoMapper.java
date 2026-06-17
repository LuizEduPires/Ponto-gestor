package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Dtos.Response.ItemsPedidoResponse;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {

    @Mapping(target = "dataPrazo", expression = "java(toTimestamp(request.dataPrazo()))")
    @Mapping(target = "dataEntrega", expression = "java(toTimestamp(request.dataEntrega()))")
    @Mapping(target = "dataProva", expression = "java(toTimestamp(request.dataProva()))")
    ItemsPedido toModel(ItemsPedidoRequest request);

    @Mapping(target = "dataPrazo", expression = "java(toLocalDateTime(item.getDataPrazo()))")
    @Mapping(target = "dataEntrega", expression = "java(toLocalDateTime(item.getDataEntrega()))")
    @Mapping(target = "dataProva", expression = "java(toLocalDateTime(item.getDataProva()))")
    ItemsPedidoResponse toResponse(ItemsPedido item);

    default com.google.cloud.Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;

        return com.google.cloud.Timestamp.of(
                java.util.Date.from(
                        dateTime.atZone(java.time.ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }

    default java.time.LocalDateTime toLocalDateTime(com.google.cloud.Timestamp timestamp) {
        if (timestamp == null) return null;

        return timestamp.toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}