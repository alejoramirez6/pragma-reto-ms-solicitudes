package com.pragma.crediya.solicitudes.infrastructure.adapters.output.client;

import com.pragma.crediya.solicitudes.domain.ports.out.UserClientPort;
import com.pragma.crediya.solicitudes.infrastructure.adapters.output.client.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Adaptador para la comunicación HTTP con el microservicio de usuarios (HU1)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {
    @Override
    public Mono<UserInfo> obtenerUsuarioConToken(String documentoIdentidad, String jwtToken) {
        log.debug("Obteniendo información del usuario con documento: {}", documentoIdentidad);
        String url = userServiceBaseUrl + getUserByDocumentEndpoint;
        var requestSpec = webClient.get().uri(url, documentoIdentidad);
        if (jwtToken != null && !jwtToken.isBlank()) {
            requestSpec = requestSpec.header("Authorization", jwtToken);
        }
        return requestSpec
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .map(this::mapToUserInfo)
                .doOnSuccess(user -> log.debug("Usuario obtenido exitosamente: {}", user.documentoIdentidad()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado con documento: {}", documentoIdentidad);
                        return Mono.empty();
                    } else {
                        log.error("Error HTTP al obtener usuario con documento {}: {} - {}", 
                                documentoIdentidad, ex.getStatusCode(), ex.getMessage());
                        return Mono.error(ex);
                    }
                })
                .doOnError(Exception.class, ex -> 
                    log.error("Error inesperado al obtener usuario con documento {}: {}", 
                            documentoIdentidad, ex.getMessage()));
    }

    private final WebClient webClient;
    
    @Value("${app.services.user-service.base-url:http://localhost:8080}")
    private String userServiceBaseUrl;
    
    @Value("${app.services.user-service.endpoints.get-by-document:/api/v1/usuarios/documento/{documento}}")
    private String getUserByDocumentEndpoint;

    @Override
    public Mono<Boolean> existeUsuario(String documentoIdentidad) {
        log.debug("Verificando existencia de usuario con documento: {}", documentoIdentidad);
        
        return obtenerUsuario(documentoIdentidad)
                .map(user -> true)
                .onErrorReturn(false)
                .doOnSuccess(exists -> log.debug("Usuario con documento {} existe: {}", documentoIdentidad, exists))
                .doOnError(error -> log.warn("Error al verificar usuario con documento {}: {}", 
                          documentoIdentidad, error.getMessage()));
    }

    @Override
    public Mono<UserInfo> obtenerUsuario(String documentoIdentidad) {
        log.debug("Obteniendo información del usuario con documento: {}", documentoIdentidad);
        
        String url = userServiceBaseUrl + getUserByDocumentEndpoint;
        
        return webClient.get()
                .uri(url, documentoIdentidad)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .map(this::mapToUserInfo)
                .doOnSuccess(user -> log.debug("Usuario obtenido exitosamente: {}", user.documentoIdentidad()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado con documento: {}", documentoIdentidad);
                        return Mono.empty(); // Retorna Mono vacío para que switchIfEmpty funcione
                    } else {
                        log.error("Error HTTP al obtener usuario con documento {}: {} - {}", 
                                documentoIdentidad, ex.getStatusCode(), ex.getMessage());
                        return Mono.error(ex); // Propaga otros errores HTTP
                    }
                })
                .doOnError(Exception.class, ex -> 
                    log.error("Error inesperado al obtener usuario con documento {}: {}", 
                            documentoIdentidad, ex.getMessage()));
    }
    
    private UserInfo mapToUserInfo(UserResponseDto dto) {
    return new UserClientPort.UserInfo(
        dto.getId(),
        dto.getNombres(),
        dto.getApellidos(),
        dto.getCorreoElectronico(),
        dto.getDocumentoIdentidad(),
        dto.getSalarioBase()
    );
    }
}
