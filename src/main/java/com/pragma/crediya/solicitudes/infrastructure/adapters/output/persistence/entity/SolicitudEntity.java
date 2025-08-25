package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity;

import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitudes_prestamo")
public class SolicitudEntity {

    @Id
    private Long id;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo")
    private Integer plazo;

    @Column("tipo_prestamo")
    private TipoPrestamo tipoPrestamo;

    @Column("estado")
    private EstadoSolicitud estado;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
