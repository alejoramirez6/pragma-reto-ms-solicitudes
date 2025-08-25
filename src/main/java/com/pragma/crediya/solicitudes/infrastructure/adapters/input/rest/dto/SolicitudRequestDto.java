package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto;

import com.pragma.crediya.solicitudes.domain.model.TipoPrestamo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudRequestDto {

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "100000.0", message = "El monto mínimo es $100,000")
    @DecimalMax(value = "500000000.0", message = "El monto máximo es $500,000,000")
    private BigDecimal monto;

    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es 6 meses")
    @Max(value = 360, message = "El plazo máximo es 360 meses (30 años)")
    private Integer plazo;

    @NotNull(message = "El tipo de préstamo es obligatorio")
    private TipoPrestamo tipoPrestamo;
}
