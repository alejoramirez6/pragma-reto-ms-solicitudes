package com.pragma.crediya.solicitudes.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI (Swagger) para documentar la API REST del microservicio.
 * 
 * ¿Qué es @Configuration?
 * - Es una anotación de Spring que indica que esta clase contiene métodos que definen beans
 * - Los métodos marcados con @Bean serán gestionados por el contenedor de Spring
 * - Se ejecuta al iniciar la aplicación para configurar componentes
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define la configuración principal de la documentación OpenAPI.
     * 
     * ¿Qué es @Bean?
     * - Indica que este método produce un bean que será gestionado por Spring
     * - El valor retornado se registra como un bean en el contexto de Spring
     * - Otros componentes pueden usar este bean mediante inyección de dependencias
     * 
     * @return OpenAPI configuración completa de la documentación
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Proporciona el token JWT obtenido del servicio de autenticación. " +
                                           "Formato: Bearer <tu_token_jwt>")))
                .info(new Info()
                        .title("CrediYa - Microservicio de Solicitudes")
                        .description("API REST para gestionar solicitudes de préstamos en la plataforma CrediYa. " +
                                   "Permite crear solicitudes de préstamo con validaciones de negocio.\n\n" +
                                   "**AUTENTICACIÓN:** Todos los endpoints requieren un token JWT válido. " +
                                   "Haz clic en el botón 'Authorize' para ingresar tu token.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo CrediYa")
                                .email("support@crediya.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.crediya.com")
                                .description("Servidor de producción")
                ));
    }
}