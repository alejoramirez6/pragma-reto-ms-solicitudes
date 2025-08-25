package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ISolicitudRequestMapper {
    
    SolicitudPrestamo toSolicitud(SolicitudRequestDto solicitudRequestDto);
}
