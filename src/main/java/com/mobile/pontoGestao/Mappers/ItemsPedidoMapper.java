package com.mobile.pontoGestao.Mappers;

import com.google.cloud.Timestamp;
import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {

    @Mapping(target = "dataPrazo", expression = "java(toTimestamp(request.dataPrazo()))")
    @Mapping(target = "dataProva", ignore = true)
    @Mapping(target = "dataEntrega", ignore = true)
    ItemsPedido toModel(ItemsPedidoRequest request);

    default Timestamp toTimestamp(Long millis) {
        if (millis == null) return null;
        return Timestamp.ofTimeMicroseconds(millis * 1000);
    }
}