package com.pragma.crediya.solicitudes.domain.ports.out;

import reactor.core.publisher.Mono;

/**
 * Puerto de salida para la comunicación con el microservicio de usuarios
 */
public interface UserClientPort {
    Mono<UserInfo> obtenerUsuarioConToken(String documentoIdentidad, String jwtToken);
    
    /**
     * Verifica si existe un usuario con el documento de identidad especificado
     * 
     * @param documentoIdentidad Documento de identidad del usuario
     * @return Mono<Boolean> que emite true si el usuario existe, false en caso contrario
     */
    Mono<Boolean> existeUsuario(String documentoIdentidad);
    
    /**
     * Obtiene la información completa de un usuario por documento de identidad
     * 
     * @param documentoIdentidad Documento de identidad del usuario
     * @return Mono<UserInfo> con la información del usuario
     */
    Mono<UserInfo> obtenerUsuario(String documentoIdentidad);
    
    /**
     * Clase interna para encapsular información básica del usuario
     */
    record UserInfo(
        Long id,
        String nombres,
        String apellidos,
        String correoElectronico,
        String documentoIdentidad,
        java.math.BigDecimal salarioBase
    ) {}
}
