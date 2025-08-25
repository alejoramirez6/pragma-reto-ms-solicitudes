package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("usuarios")
public class UsuarioEntity {

    @Id
    private Long id;

    @Column("nombres")
    private String nombres;

    @Column("apellidos")
    private String apellidos;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column("direccion")
    private String direccion;

    @Column("telefono")
    private String telefono;

    @Column("correo_electronico")
    private String correoElectronico;

    @Column("salario_base")
    private BigDecimal salarioBase;

    @Column("documento_identidad")
    private String documentoIdentidad;
}
