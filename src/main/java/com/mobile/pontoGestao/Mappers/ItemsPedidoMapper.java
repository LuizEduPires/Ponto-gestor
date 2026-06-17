package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {

    @Mapping(target = "dataPrazo", expression = "java(toTimestamp(request.dataPrazo()))")
    @Mapping(target = "dataEntrega", expression = "java(toTimestamp(request.dataEntrega()))")
    @Mapping(target = "dataProva", expression = "java(toTimestamp(request.dataProva()))")
    ItemsPedido toModel(ItemsPedidoRequest request);

    default com.google.cloud.Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return com.google.cloud.Timestamp.of(java.util.Date.from(
                dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
        ));
    }
}