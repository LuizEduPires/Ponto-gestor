package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.ClienteRequest;
import com.mobile.pontoGestao.Dtos.Response.ClienteResponse;
import com.mobile.pontoGestao.Models.Clientes;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    Clientes toModel(ClienteRequest request);
    ClienteResponse toResponse(Clientes model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCliente(ClienteRequest request,@MappingTarget Clientes cliente);
}
