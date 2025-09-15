package com.pragma.crediya.solicitudes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el modelo de dominio SolicitudPrestamo.
 */
@DisplayName("SolicitudPrestamo - Tests del modelo de dominio")
class SolicitudPrestamoTest {

    @Test
    @DisplayName("Debería crear solicitud usando patrón Builder")
    void deberiaCrearSolicitudUsandoPatronBuilder() {
        // Arrange & Act
        LocalDateTime fechaCreacion = LocalDateTime.now();
        LocalDateTime fechaActualizacion = LocalDateTime.now();

        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(fechaCreacion)
                .fechaActualizacion(fechaActualizacion)
                .build();

        // Assert
        assertThat(solicitud.getId()).isEqualTo(1L);
        assertThat(solicitud.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(solicitud.getMonto()).isEqualTo(new BigDecimal("5000000"));
        assertThat(solicitud.getPlazo()).isEqualTo(24);
        assertThat(solicitud.getTipoPrestamo()).isEqualTo(TipoPrestamo.PERSONAL);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(solicitud.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(solicitud.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }

    @Test
    @DisplayName("Debería crear solicitud vacía con constructor por defecto")
    void deberiaCrearSolicitudVaciaConConstructorPorDefecto() {
        // ACT
        SolicitudPrestamo solicitud = new SolicitudPrestamo();

        // Assert
        assertThat(solicitud.getId()).isNull();
        assertThat(solicitud.getDocumentoIdentidad()).isNull();
        assertThat(solicitud.getMonto()).isNull();
        assertThat(solicitud.getPlazo()).isNull();
        assertThat(solicitud.getTipoPrestamo()).isNull();
        assertThat(solicitud.getEstado()).isNull();
        assertThat(solicitud.getFechaCreacion()).isNull();
        assertThat(solicitud.getFechaActualizacion()).isNull();
    }

    @Test
    @DisplayName("Debería permitir modificar propiedades con setters")
    void deberiaPermitirModificarPropiedadesConSetters() {
        // Arrange
        SolicitudPrestamo solicitud = new SolicitudPrestamo();

        // ACT
        solicitud.setId(1L);
        solicitud.setDocumentoIdentidad("12345678");
        solicitud.setMonto(new BigDecimal("3000000"));
        solicitud.setPlazo(12);
        solicitud.setTipoPrestamo(TipoPrestamo.VEHICULAR);
        solicitud.setEstado(EstadoSolicitud.APROBADA);

        // Assert
        assertThat(solicitud.getId()).isEqualTo(1L);
        assertThat(solicitud.getDocumentoIdentidad()).isEqualTo("12345678");
        assertThat(solicitud.getMonto()).isEqualTo(new BigDecimal("3000000"));
        assertThat(solicitud.getPlazo()).isEqualTo(12);
        assertThat(solicitud.getTipoPrestamo()).isEqualTo(TipoPrestamo.VEHICULAR);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
    }

    @Test
    @DisplayName("Debería manejar correctamente equals y hashCode")
    void deberiaManejarCorrectamenteEqualsYHashCode() {
        // Arrange
        SolicitudPrestamo solicitud1 = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .build();

        SolicitudPrestamo solicitud2 = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .build();

        SolicitudPrestamo solicitud3 = SolicitudPrestamo.builder()
                .id(2L)
                .documentoIdentidad("87654321")
                .monto(new BigDecimal("3000000"))
                .build();

        // Assert
        assertThat(solicitud1).isEqualTo(solicitud2);
        assertThat(solicitud1).isNotEqualTo(solicitud3);
        assertThat(solicitud1.hashCode()).isEqualTo(solicitud2.hashCode());
        assertThat(solicitud1.hashCode()).isNotEqualTo(solicitud3.hashCode());
    }

    @Test
    @DisplayName("Debería generar toString readable")
    void deberiaGenerarToStringReadable() {
        // Arrange
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .build();

        // ACT
        String toString = solicitud.toString();

        // Assert
        assertThat(toString).contains("SolicitudPrestamo");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("documentoIdentidad=12345678");
        assertThat(toString).contains("monto=5000000");
        assertThat(toString).contains("tipoPrestamo=PERSONAL");
    }
}
