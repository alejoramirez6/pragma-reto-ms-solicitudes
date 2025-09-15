package com.pragma.crediya.solicitudes.infrastructure.configuration;

import com.pragma.crediya.solicitudes.infrastructure.security.JwtErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtErrorHandler jwtErrorHandler;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos para documentación y salud
                        .pathMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // Todos los endpoints de la API requieren autenticación
                        .pathMatchers("/api/v1/**").authenticated()
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(jwtErrorHandler)
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            log.info("Configurando JWT decoder con clave secreta Base64 del ms-autenticacion");
            // IMPORTANTE: Decodificar desde Base64 (como requiere ms-autenticacion)
            byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
            ReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
            log.info("JWT decoder configurado exitosamente para compatibilidad con ms-autenticacion");
            return decoder;
        } catch (Exception e) {
            log.error("Error configurando JWT decoder: {}", e.getMessage(), e);
            throw new RuntimeException("Error configurando JWT decoder", e);
        }
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // IMPORTANTE: ms-autenticacion usa "rol" (string), no "roles" (array)
        grantedAuthoritiesConverter.setAuthoritiesClaimName("rol");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
            // Log para depuración: mostrar el claim 'rol' y las autoridades extraídas
            jwtAuthenticationConverter.setPrincipalClaimName("sub");
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
                Object rolClaim = jwt.getClaims().get("rol");
                log.info("[SECURITY] Claim 'rol' recibido en JWT: {}", rolClaim);
                var authorities = grantedAuthoritiesConverter.convert(jwt);
                log.info("[SECURITY] Autoridades extraídas: {}", authorities);
                return authorities;
            });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
