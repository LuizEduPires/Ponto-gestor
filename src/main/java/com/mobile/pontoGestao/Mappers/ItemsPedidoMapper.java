package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemsPedidoMapper {
    ItemsPedido toModel(ItemsPedidoRequest request);
}
