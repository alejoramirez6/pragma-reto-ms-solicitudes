package com.pragma.crediya.solicitudes.domain.usecase;

import com.pragma.crediya.solicitudes.domain.exception.ClienteNoEncontradoException;
import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.in.ISolicitudServicePort;
import com.pragma.crediya.solicitudes.domain.ports.out.ISolicitudRepositoryPort;
import com.pragma.crediya.solicitudes.domain.ports.out.UserClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
// @RequiredArgsConstructor
public class SolicitudUseCase implements ISolicitudServicePort {
    @Override
    public Mono<SolicitudPrestamo> crearSolicitudConToken(SolicitudPrestamo solicitud, String jwtToken) {
        log.info("Iniciando creación de solicitud de préstamo - Documento: {}, Tipo: {}, Monto: {}", 
                solicitud.getDocumentoIdentidad(), solicitud.getTipoPrestamo(), solicitud.getMonto());
        return userClientPort.obtenerUsuarioConToken(solicitud.getDocumentoIdentidad(), jwtToken)
                .doOnNext(usuario -> log.debug("Usuario obtenido desde HU1 - Documento: {}, Email: {}", 
                        usuario.documentoIdentidad(), usuario.correoElectronico()))
                .switchIfEmpty(Mono.error(new ClienteNoEncontradoException(
                        "No se encontró un cliente registrado con el documento de identidad: " + 
                        solicitud.getDocumentoIdentidad())))
                .flatMap(usuario -> {
                    solicitud.setEmail(usuario.correoElectronico());
                    solicitud.setNombre((usuario.nombres() != null ? usuario.nombres() : "") + " " + (usuario.apellidos() != null ? usuario.apellidos() : ""));
                    solicitud.setSalarioBase(usuario.salarioBase() != null ? usuario.salarioBase() : java.math.BigDecimal.ZERO);
                    solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                    solicitud.setFechaCreacion(LocalDateTime.now());
                    solicitud.setFechaActualizacion(LocalDateTime.now());
                    log.debug("Cliente validado con email {}, nombre {}, salarioBase {}, procediendo a guardar solicitud", usuario.correoElectronico(), solicitud.getNombre(), solicitud.getSalarioBase());
                    return solicitudRepositoryPort.guardarSolicitud(solicitud);
                })
                .as(transactionalOperator::transactional)
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

        private final ISolicitudRepositoryPort solicitudRepositoryPort;
        private final UserClientPort userClientPort;
        private final TransactionalOperator transactionalOperator;

        public SolicitudUseCase(ISolicitudRepositoryPort solicitudRepositoryPort, UserClientPort userClientPort, TransactionalOperator transactionalOperator) {
                this.solicitudRepositoryPort = solicitudRepositoryPort;
                this.userClientPort = userClientPort;
                this.transactionalOperator = transactionalOperator;
        }

    @Override
    public Mono<SolicitudPrestamo> crearSolicitud(SolicitudPrestamo solicitud) {
        log.info("Iniciando creación de solicitud de préstamo - Documento: {}, Tipo: {}, Monto: {}", 
                solicitud.getDocumentoIdentidad(), solicitud.getTipoPrestamo(), solicitud.getMonto());

                return userClientPort.obtenerUsuario(solicitud.getDocumentoIdentidad())
                                .doOnNext(usuario -> log.debug("Usuario obtenido desde HU1 - Documento: {}, Email: {}", 
                                                usuario.documentoIdentidad(), usuario.correoElectronico()))
                                .switchIfEmpty(Mono.error(new ClienteNoEncontradoException(
                                        "No se encontró un cliente registrado con el documento de identidad: " + 
                                        solicitud.getDocumentoIdentidad())))
                                .flatMap(usuario -> {
                                        // Asignar datos completos del usuario
                                        solicitud.setEmail(usuario.correoElectronico());
                                            solicitud.setNombre((usuario.nombres() != null ? usuario.nombres() : "") + " " + (usuario.apellidos() != null ? usuario.apellidos() : ""));
                                            // Asignar salarioBase si está presente, si no poner 0
                                            solicitud.setSalarioBase(usuario.salarioBase() != null ? usuario.salarioBase() : java.math.BigDecimal.ZERO);
                                        // Si tienes lógica para tasaInteres, asígnala aquí
                                        // solicitud.setTasaInteres(...);

                                        // Establecer estado inicial y fechas
                                        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                                        solicitud.setFechaCreacion(LocalDateTime.now());
                                        solicitud.setFechaActualizacion(LocalDateTime.now());

                                        log.debug("Cliente validado con email {}, nombre {}, salarioBase {}, procediendo a guardar solicitud", usuario.correoElectronico(), solicitud.getNombre(), solicitud.getSalarioBase());
                                        return solicitudRepositoryPort.guardarSolicitud(solicitud);
                                })
                                .as(transactionalOperator::transactional)
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

    @Override
    public Flux<SolicitudPrestamo> obtenerTodasLasSolicitudes() {
        log.info("Obteniendo todas las solicitudes de préstamo");
        
        return solicitudRepositoryPort.obtenerTodasLasSolicitudes()
                .doOnNext(solicitud -> log.debug("Solicitud obtenida - ID: {}, Cliente: {}, Estado: {}", 
                        solicitud.getId(), solicitud.getDocumentoIdentidad(), solicitud.getEstado()))
                .doOnComplete(() -> log.info("Consulta de todas las solicitudes completada"))
                .doOnError(error -> log.error("Error al obtener todas las solicitudes: {}", error.getMessage(), error));
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerSolicitudesPorCliente(String documentoIdentidad) {
        log.info("Obteniendo solicitudes para el cliente con documento: {}", documentoIdentidad);
        
        return solicitudRepositoryPort.obtenerSolicitudesPorCliente(documentoIdentidad)
                .doOnNext(solicitud -> log.debug("Solicitud encontrada para cliente {} - ID: {}, Estado: {}", 
                        documentoIdentidad, solicitud.getId(), solicitud.getEstado()))
                .doOnComplete(() -> log.info("Consulta de solicitudes para cliente {} completada", documentoIdentidad))
                .doOnError(error -> log.error("Error al obtener solicitudes para cliente {}: {}", 
                        documentoIdentidad, error.getMessage(), error));
    }
        public Flux<com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.RevisionSolicitudResponseDto> obtenerSolicitudesRevision(String estado, int page, int size) {
                log.info("Obteniendo solicitudes para revisión manual - estado: {}, page: {}, size: {}", estado, page, size);
                Flux<SolicitudPrestamo> solicitudesFlux = (estado == null || estado.isEmpty())
                        ? solicitudRepositoryPort.obtenerTodasLasSolicitudes()
                        : solicitudRepositoryPort.obtenerTodasLasSolicitudes().filter(s -> s.getEstado().name().equalsIgnoreCase(estado));

                return solicitudesFlux
                        .skip((long) page * size)
                        .take(size)
                        .flatMap(solicitud -> calcularDeudaTotalMensualAprobada(solicitud.getDocumentoIdentidad())
                                .map(deudaTotal -> mapToRevisionDto(solicitud, deudaTotal))
                        );
        }

        private Mono<java.math.BigDecimal> calcularDeudaTotalMensualAprobada(String documentoIdentidad) {
                return solicitudRepositoryPort.obtenerSolicitudesPorCliente(documentoIdentidad)
                        .filter(s -> s.getEstado() == com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud.APROBADA)
                        .map(com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo::getMonto)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }

        private com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.RevisionSolicitudResponseDto mapToRevisionDto(com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo solicitud, java.math.BigDecimal deudaTotal) {
                return com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.RevisionSolicitudResponseDto.builder()
                        .id(solicitud.getId())
                        .monto(solicitud.getMonto())
                        .plazo(solicitud.getPlazo())
                        .email(solicitud.getEmail())
                        .nombre(solicitud.getNombre())
                        .tipoPrestamo(solicitud.getTipoPrestamo().name())
                        .tasaInteres(solicitud.getTasaInteres())
                        .estadoSolicitud(solicitud.getEstado().name())
                        .salarioBase(solicitud.getSalarioBase())
                        .deudaTotalMensualSolicitudesAprobadas(deudaTotal)
                        .build();
        }
                }
