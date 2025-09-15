package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad que representa los estados de solicitud en la base de datos.
 * Mapea a la tabla 'estados' según el diseño oficial de Pragma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("estados")
public class EstadoEntity {
    
    @Id
    @Column("id_estado")
    private Long idEstado;
    
    @Column("nombre")
    private String nombre;
    
    @Column("descripcion")
    private String descripcion;
}
