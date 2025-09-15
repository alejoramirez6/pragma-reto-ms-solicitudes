package com.pragma.crediya.solicitudes.domain.ports.out;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISolicitudRepositoryPort {
    Mono<SolicitudPrestamo> guardarSolicitud(SolicitudPrestamo solicitud);
    Mono<Boolean> existeClientePorDocumento(String documentoIdentidad);
    Flux<SolicitudPrestamo> obtenerTodasLasSolicitudes();
    Flux<SolicitudPrestamo> obtenerSolicitudesPorCliente(String documentoIdentidad);
}
