package com.pragma.crediya.solicitudes.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationContext {

    /**
     * Obtiene el documento de identidad del usuario autenticado.
     * ms-autenticacion usa el claim "documento"
     */
    public Mono<String> getCurrentUserDocument() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(jwtToken -> {
                    Jwt jwt = jwtToken.getToken();
                    String documento = jwt.getClaimAsString("documento");
                    log.debug("Documento extraído del JWT: {}", documento);
                    return documento;
                })
                .doOnError(error -> log.error("Error extrayendo documento del contexto de autenticación: {}", error.getMessage(), error));
    }

    /**
     * Obtiene el email del usuario autenticado.
     * ms-autenticacion usa el claim "correo"
     */
    public Mono<String> getCurrentUserEmail() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(jwtToken -> {
                    Jwt jwt = jwtToken.getToken();
                    String correo = jwt.getClaimAsString("correo");
                    log.debug("Email extraído del JWT: {}", correo);
                    return correo;
                })
                .doOnError(error -> log.error("Error extrayendo email del contexto de autenticación: {}", error.getMessage(), error));
    }

    /**
     * Obtiene el rol del usuario autenticado.
     * ms-autenticacion usa el claim "rol" (string único, no array)
     */
    public Mono<String> getCurrentUserRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(jwtToken -> {
                    Jwt jwt = jwtToken.getToken();
                    String rol = jwt.getClaimAsString("rol");
                    log.debug("Rol extraído del JWT: {}", rol);
                    return rol;
                })
                .doOnError(error -> log.error("Error extrayendo rol del contexto de autenticación: {}", error.getMessage(), error));
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     */
    public Mono<Boolean> hasRole(String role) {
        return getCurrentUserRole()
                .map(userRole -> {
                    boolean hasRole = role.equals(userRole);
                    log.debug("Usuario tiene rol '{}': {}", role, hasRole);
                    return hasRole;
                })
                .defaultIfEmpty(false);
    }

    /**
     * Verifica si el usuario es ADMIN o ASESOR.
     */
    public Mono<Boolean> isAdminOrAsesor() {
        return getCurrentUserRole()
                .map(rol -> {
                    boolean isAdminOrAsesor = "ADMIN".equals(rol) || "ASESOR".equals(rol);
                    log.debug("Usuario es ADMIN o ASESOR: {}", isAdminOrAsesor);
                    return isAdminOrAsesor;
                })
                .defaultIfEmpty(false);
    }

    /**
     * Verifica si el usuario es CLIENTE.
     */
    public Mono<Boolean> isCliente() {
        return hasRole("CLIENTE");
    }
}
