package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ItemsPedidoRequest;
import com.mobile.pontoGestao.Dtos.Response.ItemsPedidoResponse;
import com.mobile.pontoGestao.Models.ItemsPedido;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ItemsPedidoMapper {

    ItemsPedido toModel(ItemsPedidoRequest request);

    ItemsPedidoResponse toDto(ItemsPedido model);

    void updateFromRequest(
            ItemsPedidoRequest request,
            @MappingTarget ItemsPedido item
    );
}