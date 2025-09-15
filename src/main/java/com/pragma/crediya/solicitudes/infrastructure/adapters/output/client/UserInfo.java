package com.pragma.crediya.solicitudes.infrastructure.adapters.output.client;

/**
 * Record que representa la información del usuario obtenida desde HU1
 */
public record UserInfo(
    Long id,
    String nombres,
    String apellidos,
    String correoElectronico,
    String documentoIdentidad
) {
}
