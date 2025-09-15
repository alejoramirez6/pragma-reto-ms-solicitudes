package com.pragma.crediya.solicitudes.domain.ports.in;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISolicitudServicePort {
    Mono<SolicitudPrestamo> crearSolicitudConToken(SolicitudPrestamo solicitud, String jwtToken);
    Mono<SolicitudPrestamo> crearSolicitud(SolicitudPrestamo solicitud);
    Flux<SolicitudPrestamo> obtenerTodasLasSolicitudes();
    Flux<SolicitudPrestamo> obtenerSolicitudesPorCliente(String documentoIdentidad);
    Flux<com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.RevisionSolicitudResponseDto> obtenerSolicitudesRevision(String estado, int page, int size);
}
