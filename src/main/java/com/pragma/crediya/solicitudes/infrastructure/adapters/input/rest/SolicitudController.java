package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest;

import com.pragma.crediya.solicitudes.domain.ports.in.ISolicitudServicePort;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudRequestDto;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudResponseDto;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.mapper.ISolicitudRequestMapper;
import com.pragma.crediya.solicitudes.infrastructure.security.AuthenticationContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.RevisionSolicitudResponseDto;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador REST para gestionar solicitudes de préstamo.
 */
@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Solicitudes de Préstamo", description = "Operaciones para gestionar solicitudes de préstamo")
public class SolicitudController {
    /**
     * Endpoint para obtener solo las solicitudes en estado PENDIENTE_REVISION.
     * Accesible solo por roles ADMIN/ASESOR.
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ASESOR')")
    @Operation(summary = "Solicitudes pendientes de revisión", description = "Obtiene únicamente las solicitudes en estado PENDIENTE_REVISION. Solo ADMIN/ASESOR.")
    public Flux<RevisionSolicitudResponseDto> obtenerSolicitudesPendientes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Solicitudes pendientes de revisión - page: {}, size: {}", page, size);
        return solicitudServicePort.obtenerSolicitudesRevision("PENDIENTE_REVISION", page, size);
    }

    /**
     * INYECCIÓN DE DEPENDENCIAS:
     * - Spring busca beans que implementen estas interfaces
     * - Los inyecta automáticamente en el constructor (gracias a @RequiredArgsConstructor)
     */
    private final ISolicitudServicePort solicitudServicePort;
    private final ISolicitudRequestMapper solicitudRequestMapper;
    private final AuthenticationContext authenticationContext;
        /**
         * Endpoint para revisión manual de solicitudes con paginación y filtro por estado.
         * Solo accesible por roles ADMIN/ASESOR.
         * Devuelve información extendida para la revisión (HU4).
         */
        @GetMapping("/revision")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ASESOR')")
        @Operation(summary = "Revisión manual de solicitudes", description = "Obtiene solicitudes para revisión manual, filtrando por estado y paginando resultados. Solo ADMIN/ASESOR.")
        public Flux<RevisionSolicitudResponseDto> obtenerSolicitudesRevision(
                @RequestParam(name = "estado", required = false) String estado,
                @RequestParam(name = "page", defaultValue = "0") int page,
                @RequestParam(name = "size", defaultValue = "10") int size) {
            log.info("Revisión manual de solicitudes - estado: {}, page: {}, size: {}", estado, page, size);
            return solicitudServicePort.obtenerSolicitudesRevision(estado, page, size);
        }

    /**
     * Endpoint para crear una nueva solicitud de préstamo.
     * 
     * FLUJO DE PROCESAMIENTO:
     * 1. HTTP POST llega a /api/v1/solicitud
     * 2. Spring deserializa JSON a SolicitudRequestDto
     * 3. @Valid activa validaciones de Bean Validation
     * 4. Se mapea DTO a modelo de dominio
     * 5. Se ejecuta lógica de negocio
     * 6. Se retorna respuesta HTTP 201 con la solicitud creada
     * 
     * @param solicitudRequestDto Datos de la solicitud desde el cliente
     * @return Mono<ResponseEntity<SolicitudPrestamo>> Respuesta reactiva con la solicitud creada
     */
    @PostMapping
    @Operation(
        summary = "Crear solicitud de préstamo",
        description = "Crea una nueva solicitud de préstamo validando la información del cliente y los datos del préstamo. " +
                     "La solicitud se registra automáticamente con estado 'Pendiente de revisión'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Solicitud creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SolicitudResponseDto.class),
                examples = @ExampleObject(
                    name = "Solicitud creada",
                    value = """
                        {
                            "mensaje": "Solicitud creada con éxito",
                            "estado": "EXITOSO",
                            "solicitud": {
                                "id": 1,
                                "documentoIdentidad": "12345678",
                                "email": "alejoramirez6@yahoo.com",
                                "monto": 5000000.00,
                                "plazo": 24,
                                "tipoPrestamo": "PERSONAL",
                                "estado": "PENDIENTE_REVISION",
                                "fechaCreacion": "2025-08-27T10:30:00",
                                "fechaActualizacion": "2025-08-27T10:30:00"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Errores de validación en los datos enviados",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de validación",
                    value = """
                        {
                            "timestamp": "2025-08-27T10:30:00",
                            "status": 400,
                            "error": "Errores de validación",
                            "validationErrors": {
                                "monto": "El monto mínimo es $100,000",
                                "documentoIdentidad": "El documento de identidad es obligatorio"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Cliente no encontrado",
                    value = """
                        {
                            "timestamp": "2025-08-27T10:30:00",
                            "status": 404,
                            "error": "Cliente no encontrado",
                            "message": "No se encontró un cliente registrado con el documento de identidad: 99999999"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error interno",
                    value = """
                        {
                            "timestamp": "2025-08-27T10:30:00",
                            "status": 500,
                            "error": "Error interno del servidor",
                            "message": "Ha ocurrido un error inesperado. Por favor, inténtelo más tarde."
                        }
                        """
                )
            )
        )
    })
    public Mono<ResponseEntity<SolicitudResponseDto>> crearSolicitud(
            @Parameter(
                description = "Datos de la solicitud de préstamo",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SolicitudRequestDto.class),
                    examples = @ExampleObject(
                        name = "Solicitud ejemplo",
                        value = """
                            {
                                "documentoIdentidad": "12345678",
                                "monto": 5000000.00,
                                "plazo": 24,
                                "tipoPrestamo": "PERSONAL"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody SolicitudRequestDto solicitudRequestDto,
            org.springframework.http.server.reactive.ServerHttpRequest request) {
        String jwtToken = request.getHeaders().getFirst("Authorization");
        log.info("Solicitud de préstamo recibida - Cliente: {}, Tipo: {}, Monto: {} con validación de autorización", 
                solicitudRequestDto.getDocumentoIdentidad(), 
                solicitudRequestDto.getTipoPrestamo(), 
                solicitudRequestDto.getMonto());
        return authenticationContext.isAdminOrAsesor()
                .flatMap(isAdminOrAsesor -> {
                    if (isAdminOrAsesor) {
                        log.info("Usuario ADMIN/ASESOR: puede crear solicitudes para cualquier cliente");
                        return Mono.just(solicitudRequestDto);
                    } else {
                        log.info("Usuario CLIENTE: validando autorización para crear solicitud");
                        return authenticationContext.getCurrentUserDocument()
                                .flatMap(currentUserDocument -> {
                                    if (solicitudRequestDto.getDocumentoIdentidad().equals(currentUserDocument)) {
                                        log.debug("Cliente autorizado para crear solicitud para sí mismo: {}", currentUserDocument);
                                        return Mono.just(solicitudRequestDto);
                                    } else {
                                        log.warn("Cliente {} intentó crear solicitud para otro cliente: {}", 
                                                currentUserDocument, solicitudRequestDto.getDocumentoIdentidad());
                                        return Mono.error(new SecurityException("No está autorizado para crear solicitudes para otro cliente"));
                                    }
                                });
                    }
                })
                .map(solicitudRequestMapper::toSolicitud)
                .flatMap(solicitud -> solicitudServicePort.crearSolicitudConToken(solicitud, jwtToken))
                .map(SolicitudResponseDto::crearRespuestaExitosa)
                .map(respuesta -> {
                    log.info("Solicitud creada exitosamente - ID: {}, Cliente: {}", 
                            respuesta.getSolicitud().getId(), 
                            respuesta.getSolicitud().getDocumentoIdentidad());
                    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
                });
    }

    @GetMapping
    @Operation(summary = "Obtener todas las solicitudes", 
               description = "Obtiene un listado de todas las solicitudes de préstamo en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SolicitudResponseDto.class)))
    })
    public Flux<SolicitudResponseDto> obtenerTodasLasSolicitudes() {
        log.info("Solicitud para obtener solicitudes de préstamo con autorización basada en roles");
        
        return authenticationContext.isAdminOrAsesor()
                .flatMapMany(isAdminOrAsesor -> {
                    if (isAdminOrAsesor) {
                        log.info("Usuario ADMIN/ASESOR: obteniendo todas las solicitudes");
                        return solicitudServicePort.obtenerTodasLasSolicitudes();
                    } else {
                        log.info("Usuario CLIENTE: obteniendo solo sus solicitudes");
                        return authenticationContext.getCurrentUserDocument()
                                .flatMapMany(documentoIdentidad -> {
                                    log.debug("Obteniendo solicitudes para cliente: {}", documentoIdentidad);
                                    return solicitudServicePort.obtenerSolicitudesPorCliente(documentoIdentidad);
                                });
                    }
                })
                .map(SolicitudResponseDto::crearRespuestaExitosa)
                .doOnNext(respuesta -> log.debug("Solicitud obtenida - ID: {}, Cliente: {}", 
                        respuesta.getSolicitud().getId(), 
                        respuesta.getSolicitud().getDocumentoIdentidad()))
                .doOnComplete(() -> log.info("Consulta de solicitudes completada"));
    }

    @GetMapping("/cliente/{documentoIdentidad}")
    @Operation(summary = "Obtener solicitudes por cliente", 
               description = "Obtiene todas las solicitudes de préstamo de un cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitudes del cliente obtenidas exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SolicitudResponseDto.class)))
    })
    public Flux<SolicitudResponseDto> obtenerSolicitudesPorCliente(
            @Parameter(description = "Documento de identidad del cliente", required = true)
            @PathVariable String documentoIdentidad) {
        log.info("Solicitud para obtener solicitudes del cliente con documento: {} con autorización", documentoIdentidad);
        
        return authenticationContext.isAdminOrAsesor()
                .flatMapMany(isAdminOrAsesor -> {
                    if (isAdminOrAsesor) {
                        log.info("Usuario ADMIN/ASESOR: puede acceder a solicitudes de cualquier cliente");
                        return solicitudServicePort.obtenerSolicitudesPorCliente(documentoIdentidad);
                    } else {
                        log.info("Usuario CLIENTE: verificando autorización para acceder a solicitudes");
                        return authenticationContext.getCurrentUserDocument()
                                .flatMapMany(currentUserDocument -> {
                                    if (documentoIdentidad.equals(currentUserDocument)) {
                                        log.debug("Cliente autorizado para ver sus propias solicitudes: {}", documentoIdentidad);
                                        return solicitudServicePort.obtenerSolicitudesPorCliente(documentoIdentidad);
                                    } else {
                                        log.warn("Cliente {} intentó acceder a solicitudes de otro cliente: {}", 
                                                currentUserDocument, documentoIdentidad);
                                        return Flux.empty(); // Cliente no puede ver solicitudes de otros
                                    }
                                });
                    }
                })
                .map(SolicitudResponseDto::crearRespuestaExitosa)
                .doOnNext(respuesta -> log.debug("Solicitud encontrada para cliente {} - ID: {}", 
                        documentoIdentidad, respuesta.getSolicitud().getId()))
                .doOnComplete(() -> log.info("Consulta de solicitudes para cliente {} completada", documentoIdentidad));
    }
}
