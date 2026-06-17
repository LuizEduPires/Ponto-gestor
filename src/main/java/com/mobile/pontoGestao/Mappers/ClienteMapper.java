package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ClienteRequest;
import com.mobile.pontoGestao.Dtos.Request.ClienteUpdateRequest;
import com.mobile.pontoGestao.Dtos.Response.ClienteResponse;
import com.mobile.pontoGestao.Models.Clientes;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Clientes toModel(ClienteRequest request);

    ClienteResponse toResponse(Clientes model);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateCliente(
            ClienteUpdateRequest request,
            @MappingTarget Clientes cliente
    );

    @AfterMapping
    default void preencherDescricao(
            @MappingTarget Clientes cliente
    ) {
        if (cliente.getDescricao() == null) {
            cliente.setDescricao("");
        }
    }
}