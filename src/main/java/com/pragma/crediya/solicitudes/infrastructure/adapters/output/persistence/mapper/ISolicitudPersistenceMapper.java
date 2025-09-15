package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.mapper;

import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.model.TipoPrestamo;
import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ISolicitudPersistenceMapper {
    
    @Mapping(target = "idSolicitud", source = "id")
    @Mapping(target = "idEstado", source = "estado", qualifiedByName = "estadoToId")
    @Mapping(target = "idTipoPrestamo", source = "tipoPrestamo", qualifiedByName = "tipoPrestamoToId")
    SolicitudEntity toEntity(SolicitudPrestamo solicitud);
    
    @Mapping(target = "id", source = "idSolicitud")
    @Mapping(target = "estado", source = "idEstado", qualifiedByName = "idToEstado")
    @Mapping(target = "tipoPrestamo", source = "idTipoPrestamo", qualifiedByName = "idToTipoPrestamo")
    SolicitudPrestamo toSolicitud(SolicitudEntity entity);
    
    @Named("estadoToId")
    default Long estadoToId(EstadoSolicitud estado) {
        if (estado == null) return null;
        return switch (estado) {
            case PENDIENTE_REVISION -> 1L;
            case EN_EVALUACION -> 2L;
            case APROBADA -> 3L;
            case RECHAZADA -> 4L;
            case DESEMBOLSADA -> 5L;
        };
    }
    
    @Named("idToEstado")
    default EstadoSolicitud idToEstado(Long idEstado) {
        if (idEstado == null) return null;
        return switch (idEstado.intValue()) {
            case 1 -> EstadoSolicitud.PENDIENTE_REVISION;
            case 2 -> EstadoSolicitud.EN_EVALUACION;
            case 3 -> EstadoSolicitud.APROBADA;
            case 4 -> EstadoSolicitud.RECHAZADA;
            case 5 -> EstadoSolicitud.DESEMBOLSADA;
            default -> throw new IllegalArgumentException("ID de estado desconocido: " + idEstado);
        };
    }
    
    @Named("tipoPrestamoToId")
    default Long tipoPrestamoToId(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) return null;
        return switch (tipoPrestamo) {
            case PERSONAL -> 1L;
            case VEHICULAR -> 2L;
            case HIPOTECARIO -> 3L;
            case EDUCATIVO -> 4L;
            case EMPRESARIAL -> 5L;
        };
    }
    
    @Named("idToTipoPrestamo")
    default TipoPrestamo idToTipoPrestamo(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) return null;
        return switch (idTipoPrestamo.intValue()) {
            case 1 -> TipoPrestamo.PERSONAL;
            case 2 -> TipoPrestamo.VEHICULAR;
            case 3 -> TipoPrestamo.HIPOTECARIO;
            case 4 -> TipoPrestamo.EDUCATIVO;
            case 5 -> TipoPrestamo.EMPRESARIAL;
            default -> throw new IllegalArgumentException("ID de tipo de préstamo desconocido: " + idTipoPrestamo);
        };
    }
}
