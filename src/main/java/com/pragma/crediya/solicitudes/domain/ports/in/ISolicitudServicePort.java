package com.pragma.crediya.solicitudes.domain.ports.in;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import reactor.core.publisher.Mono;

public interface ISolicitudServicePort {
    Mono<SolicitudPrestamo> crearSolicitud(SolicitudPrestamo solicitud);
}
