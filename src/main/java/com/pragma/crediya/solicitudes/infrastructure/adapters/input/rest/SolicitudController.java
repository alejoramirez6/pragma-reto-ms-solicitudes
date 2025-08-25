package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.in.ISolicitudServicePort;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudRequestDto;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.mapper.ISolicitudRequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/solicitud")
@RequiredArgsConstructor
@Slf4j
public class SolicitudController {

    private final ISolicitudServicePort solicitudServicePort;
    private final ISolicitudRequestMapper solicitudRequestMapper;

    @PostMapping
    public Mono<ResponseEntity<SolicitudPrestamo>> crearSolicitud(@Valid @RequestBody SolicitudRequestDto solicitudRequestDto) {
        log.info("Solicitud de préstamo recibida - Cliente: {}, Tipo: {}, Monto: {}", 
                solicitudRequestDto.getDocumentoIdentidad(), 
                solicitudRequestDto.getTipoPrestamo(), 
                solicitudRequestDto.getMonto());
        
        return Mono.just(solicitudRequestDto)
                .map(solicitudRequestMapper::toSolicitud)
                .flatMap(solicitudServicePort::crearSolicitud)
                .map(solicitud -> {
                    log.info("Solicitud creada exitosamente - ID: {}, Estado: {}", 
                            solicitud.getId(), solicitud.getEstado());
                    return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
                });
    }
}
