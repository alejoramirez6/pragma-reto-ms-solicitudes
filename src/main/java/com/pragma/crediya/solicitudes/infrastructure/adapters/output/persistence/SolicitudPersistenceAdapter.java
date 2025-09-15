package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.out.ISolicitudRepositoryPort;
import com.pragma.crediya.solicitudes.domain.ports.out.UserClientPort;
import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.mapper.ISolicitudPersistenceMapper;
import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.repository.ISolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudPersistenceAdapter implements ISolicitudRepositoryPort {

    private final ISolicitudRepository solicitudRepository;
    private final UserClientPort userClientPort;
    private final ISolicitudPersistenceMapper solicitudMapper;

    @Override
    @Transactional
    public Mono<SolicitudPrestamo> guardarSolicitud(SolicitudPrestamo solicitud) {
        log.debug("Guardando solicitud en base de datos - Cliente: {}, Tipo: {}", 
                solicitud.getDocumentoIdentidad(), solicitud.getTipoPrestamo());
        
        return Mono.just(solicitud)
                .map(solicitudMapper::toEntity)
                .doOnNext(entity -> log.debug("Entidad de solicitud mapeada para persistencia"))
                .flatMap(solicitudRepository::save)
                .doOnNext(savedEntity -> log.debug("Solicitud guardada con ID: {}", savedEntity.getIdSolicitud()))
                .map(solicitudMapper::toSolicitud)
                .doOnError(error -> log.error("Error al guardar solicitud en base de datos: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existeClientePorDocumento(String documentoIdentidad) {
        log.debug("Verificando si existe cliente con documento: {}", documentoIdentidad);
        
        return userClientPort.existeUsuario(documentoIdentidad)
                .doOnNext(existe -> log.debug("Resultado verificación documento {}: {}", documentoIdentidad, existe))
                .doOnError(error -> log.error("Error al verificar existencia de documento {}: {}", documentoIdentidad, error.getMessage(), error));
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerTodasLasSolicitudes() {
        log.debug("Consultando todas las solicitudes de la base de datos");
        
        return solicitudRepository.findAll()
                .doOnNext(entity -> log.debug("Solicitud encontrada - ID: {}", entity.getIdSolicitud()))
                .map(solicitudMapper::toSolicitud)
                .doOnComplete(() -> log.debug("Consulta de todas las solicitudes completada"))
                .doOnError(error -> log.error("Error al consultar todas las solicitudes: {}", error.getMessage(), error));
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerSolicitudesPorCliente(String documentoIdentidad) {
        log.debug("Consultando solicitudes para cliente con documento: {}", documentoIdentidad);
        
        return solicitudRepository.findByDocumentoIdentidad(documentoIdentidad)
                .doOnNext(entity -> log.debug("Solicitud encontrada para cliente {} - ID: {}", 
                        documentoIdentidad, entity.getIdSolicitud()))
                .map(solicitudMapper::toSolicitud)
                .doOnComplete(() -> log.debug("Consulta de solicitudes para cliente {} completada", documentoIdentidad))
                .doOnError(error -> log.error("Error al consultar solicitudes para cliente {}: {}", 
                        documentoIdentidad, error.getMessage(), error));
    }
}
