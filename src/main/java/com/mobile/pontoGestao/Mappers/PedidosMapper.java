package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.PedidoRequest;
import com.mobile.pontoGestao.Dtos.Request.PedidoRequestUpdate;
import com.mobile.pontoGestao.Dtos.Response.PedidoResponse;
import com.mobile.pontoGestao.Models.Pedidos;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PedidosMapper {
    Pedidos toModel(PedidoRequest request);
    PedidoResponse toDto(Pedidos pedido);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePedido(PedidoRequestUpdate request,@MappingTarget Pedidos pedido);
}
