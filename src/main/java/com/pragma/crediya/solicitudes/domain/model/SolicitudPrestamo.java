package com.pragma.crediya.solicitudes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPrestamo {
    
    private Long id;
    private String documentoIdentidad;
    private BigDecimal monto;
    private Integer plazo; // en meses
    private TipoPrestamo tipoPrestamo;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
