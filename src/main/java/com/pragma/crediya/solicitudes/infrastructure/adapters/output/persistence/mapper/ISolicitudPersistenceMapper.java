package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.mapper;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ISolicitudPersistenceMapper {
    
    SolicitudEntity toEntity(SolicitudPrestamo solicitud);
    SolicitudPrestamo toSolicitud(SolicitudEntity entity);
}
