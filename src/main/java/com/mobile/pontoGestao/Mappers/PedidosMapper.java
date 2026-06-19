package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Models.Pedidos;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {ItemsPedidoMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PedidosMapper {

    Pedidos toModel(PedidoRequest request);

    PedidoResponse toDto(Pedidos pedido);

    void updateFromRequest(
            PedidoRequestUpdate request,
            @MappingTarget Pedidos pedido
    );
}