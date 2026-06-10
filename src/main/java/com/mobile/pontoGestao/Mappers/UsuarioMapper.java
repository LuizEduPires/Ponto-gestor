package com.mobile.pontoGestao.Mappers;

import com.mobile.pontoGestao.Dtos.Request.UsuarioRequest;
import com.mobile.pontoGestao.Dtos.Request.UsuarioUpdate;
import com.mobile.pontoGestao.Dtos.Response.UsuarioResponse;
import com.mobile.pontoGestao.Models.Usuarios;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuarios toModel(UsuarioRequest dto);
    UsuarioResponse toResponse(Usuarios model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUsuario(UsuarioUpdate dto, @MappingTarget Usuarios model);
}
