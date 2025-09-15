package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto;

import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta exitosa de creación de solicitud de préstamo.
 * Incluye un mensaje de éxito para mejorar la experiencia del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta exitosa de creación de solicitud de préstamo")
public class SolicitudResponseDto {

    @Schema(description = "Mensaje de confirmación", example = "Solicitud creada con éxito")
    private String mensaje;

    @Schema(description = "Código de estado", example = "EXITOSO")
    private String estado;

    @Schema(description = "Datos de la solicitud creada")
    private DatosSolicitud solicitud;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información detallada de la solicitud creada")
    public static class DatosSolicitud {
        
        @Schema(description = "ID único de la solicitud", example = "10")
        private Long id;

        @Schema(description = "Documento de identidad del cliente", example = "1093749043")
        private String documentoIdentidad;

        @Schema(description = "Email del cliente obtenido automáticamente", example = "alejoramirez6@yahoo.com")
        private String email;

        @Schema(description = "Monto solicitado", example = "5000000.00")
        private BigDecimal monto;

        @Schema(description = "Plazo en meses", example = "24")
        private Integer plazo;

        @Schema(description = "Tipo de préstamo", example = "PERSONAL")
        private String tipoPrestamo;

        @Schema(description = "Estado de la solicitud", example = "PENDIENTE_REVISION")
        private String estado;

        @Schema(description = "Fecha de creación", example = "2025-08-28T16:22:05.840873")
        private LocalDateTime fechaCreacion;

        @Schema(description = "Fecha de última actualización", example = "2025-08-28T16:22:05.840873")
        private LocalDateTime fechaActualizacion;
    }

    /**
     * Método de utilidad para crear una respuesta exitosa a partir de una solicitud.
     */
    public static SolicitudResponseDto crearRespuestaExitosa(SolicitudPrestamo solicitud) {
        return SolicitudResponseDto.builder()
                .mensaje("Solicitud creada con éxito")
                .estado("EXITOSO")
                .solicitud(DatosSolicitud.builder()
                        .id(solicitud.getId())
                        .documentoIdentidad(solicitud.getDocumentoIdentidad())
                        .email(solicitud.getEmail())
                        .monto(solicitud.getMonto())
                        .plazo(solicitud.getPlazo())
                        .tipoPrestamo(solicitud.getTipoPrestamo().name())
                        .estado(solicitud.getEstado().name())
                        .fechaCreacion(solicitud.getFechaCreacion())
                        .fechaActualizacion(solicitud.getFechaActualizacion())
                        .build())
                .build();
    }
}
