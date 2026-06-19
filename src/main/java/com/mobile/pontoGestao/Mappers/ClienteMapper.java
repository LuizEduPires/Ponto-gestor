package com.mobile.pontoGestao.Mappers;

import com.google.cloud.Timestamp;
import com.mobile.pontoGestao.Dtos.Request.ClienteRequest;
import com.mobile.pontoGestao.Dtos.Request.ClienteUpdateRequest;
import com.mobile.pontoGestao.Dtos.Response.ClienteResponse;
import com.mobile.pontoGestao.Models.Clientes;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Clientes toModel(ClienteRequest request);

    @Mapping(
            target = "dataCriacao",
            expression = "java(toLocalDateTime(model.getDataCriacao()))"
    )
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

    default LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}