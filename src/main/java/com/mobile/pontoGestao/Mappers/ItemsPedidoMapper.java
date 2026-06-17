package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {

    @Mapping(target = "dataProva", ignore = true)
    @Mapping(target = "dataEntrega", ignore = true)
    ItemsPedido toModel(ItemsPedidoRequest request);
}