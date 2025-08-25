package com.pragma.crediya.solicitudes.domain.usecase;

import com.pragma.crediya.solicitudes.domain.exception.ClienteNoEncontradoException;
import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.in.ISolicitudServicePort;
import com.pragma.crediya.solicitudes.domain.ports.out.ISolicitudRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class SolicitudUseCase implements ISolicitudServicePort {

    private final ISolicitudRepositoryPort solicitudRepositoryPort;

    @Override
    @Transactional
    public Mono<SolicitudPrestamo> crearSolicitud(SolicitudPrestamo solicitud) {
        log.info("Iniciando creación de solicitud de préstamo - Documento: {}, Tipo: {}, Monto: {}", 
                solicitud.getDocumentoIdentidad(), solicitud.getTipoPrestamo(), solicitud.getMonto());

        return solicitudRepositoryPort.existeClientePorDocumento(solicitud.getDocumentoIdentidad())
                .doOnNext(existe -> log.debug("Verificación de cliente - Documento {}: Existe: {}", 
                        solicitud.getDocumentoIdentidad(), existe))
                .flatMap(existe -> {
                    if (Boolean.FALSE.equals(existe)) {
                        log.warn("Cliente no encontrado con documento: {}", solicitud.getDocumentoIdentidad());
                        return Mono.error(new ClienteNoEncontradoException(
                            "No se encontró un cliente registrado con el documento de identidad: " + 
                            solicitud.getDocumentoIdentidad()
                        ));
                    }

                    // Establecer estado inicial y fechas
                    solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                    solicitud.setFechaCreacion(LocalDateTime.now());
                    solicitud.setFechaActualizacion(LocalDateTime.now());

                    log.debug("Cliente validado, procediendo a guardar solicitud");
                    return solicitudRepositoryPort.guardarSolicitud(solicitud);
                })
                .doOnSuccess(solicitudGuardada -> 
                    log.info("Solicitud de préstamo creada exitosamente - ID: {}, Cliente: {}, Estado: {}", 
                        solicitudGuardada.getId(), solicitudGuardada.getDocumentoIdentidad(), 
                        solicitudGuardada.getEstado())
                )
                .doOnError(error -> 
                    log.error("Error durante la creación de solicitud para cliente {}: {}", 
                        solicitud.getDocumentoIdentidad(), error.getMessage(), error)
                );
    }
}
