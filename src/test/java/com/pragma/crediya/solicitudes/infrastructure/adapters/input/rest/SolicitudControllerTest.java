package com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest;

import com.pragma.crediya.solicitudes.domain.exception.ClienteNoEncontradoException;
import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.model.TipoPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.in.ISolicitudServicePort;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudRequestDto;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.dto.SolicitudResponseDto;
import com.pragma.crediya.solicitudes.infrastructure.adapters.input.rest.mapper.ISolicitudRequestMapper;
import com.pragma.crediya.solicitudes.infrastructure.security.AuthenticationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para SolicitudController.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SolicitudController - Tests unitarios")
class SolicitudControllerTest {

    @Mock
    private ISolicitudServicePort solicitudServicePort;

    @Mock
    private ISolicitudRequestMapper solicitudRequestMapper;

    @Mock
    private AuthenticationContext authenticationContext;

    @InjectMocks
    private SolicitudController solicitudController;

    private SolicitudRequestDto solicitudRequestDto;
    private SolicitudPrestamo solicitudDominio;
    private SolicitudPrestamo solicitudCreada;

    @BeforeEach
    void setUp() {
        solicitudRequestDto = new SolicitudRequestDto();
        solicitudRequestDto.setDocumentoIdentidad("12345678");
        solicitudRequestDto.setMonto(new BigDecimal("5000000"));
        solicitudRequestDto.setPlazo(24);
        solicitudRequestDto.setTipoPrestamo(TipoPrestamo.PERSONAL);

        solicitudDominio = SolicitudPrestamo.builder()
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .build();

        solicitudCreada = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        // Configurar mocks de autenticación por defecto (usuario ADMIN)
        when(authenticationContext.isAdminOrAsesor()).thenReturn(Mono.just(true));
        when(authenticationContext.getCurrentUserDocument()).thenReturn(Mono.just("admin123"));
        when(authenticationContext.getCurrentUserRole()).thenReturn(Mono.just("ADMIN"));
    }

    @Test
    @DisplayName("Debería crear solicitud exitosamente y retornar HTTP 201")
    void deberiaCrearSolicitudExitosamenteYRetornarHttp201() {
        // Arrange
        when(solicitudRequestMapper.toSolicitud(solicitudRequestDto))
                .thenReturn(solicitudDominio);
        String token = "Bearer testtoken";
        org.springframework.http.server.reactive.ServerHttpRequest mockRequest = org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/api/v1/solicitudes").header("Authorization", token).build();
        when(solicitudServicePort.crearSolicitudConToken(solicitudDominio, token))
                .thenReturn(Mono.just(solicitudCreada));

        // Act & Assert
        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestDto, mockRequest))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getMensaje()).isEqualTo("Solicitud creada con éxito");
                    assertThat(response.getBody().getEstado()).isEqualTo("EXITOSO");
                    assertThat(response.getBody().getSolicitud()).isNotNull();
                    assertThat(response.getBody().getSolicitud().getId()).isEqualTo(1L);
                    assertThat(response.getBody().getSolicitud().getDocumentoIdentidad()).isEqualTo("12345678");
                    assertThat(response.getBody().getSolicitud().getEstado()).isEqualTo("PENDIENTE_REVISION");
                    return true;
                })
                .verifyComplete();

        // VERIFY
        verify(solicitudRequestMapper).toSolicitud(solicitudRequestDto);
        verify(solicitudServicePort).crearSolicitudConToken(solicitudDominio, token);
    }

    @Test
    @DisplayName("Debería propagar excepción cuando el servicio falla")
    void deberiaPropgarExcepcionCuandoElServicioFalla() {
        // Arrange
        when(solicitudRequestMapper.toSolicitud(solicitudRequestDto))
                .thenReturn(solicitudDominio);
        String token = "Bearer testtoken";
        org.springframework.http.server.reactive.ServerHttpRequest mockRequest = org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/api/v1/solicitudes").header("Authorization", token).build();
        when(solicitudServicePort.crearSolicitudConToken(solicitudDominio, token))
                .thenReturn(Mono.error(new ClienteNoEncontradoException("Cliente no encontrado")));

        // Act & Assert
        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestDto, mockRequest))
                .expectErrorMatches(error -> {
                    assertThat(error).isInstanceOf(ClienteNoEncontradoException.class);
                    assertThat(error.getMessage()).isEqualTo("Cliente no encontrado");
                    return true;
                })
                .verify();

        verify(solicitudRequestMapper).toSolicitud(solicitudRequestDto);
        verify(solicitudServicePort).crearSolicitudConToken(solicitudDominio, token);
    }

    @Test
    @DisplayName("Debería manejar error en el mapper")
    void deberiaManejarErrorEnElMapper() {
        // Arrange
        when(solicitudRequestMapper.toSolicitud(any(SolicitudRequestDto.class)))
                .thenThrow(new RuntimeException("Error en mapeo"));

        // Act & Assert
        org.springframework.http.server.reactive.ServerHttpRequest mockRequest = org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/api/v1/solicitudes").header("Authorization", "Bearer testtoken").build();
        StepVerifier.create(solicitudController.crearSolicitud(solicitudRequestDto, mockRequest))
                .expectErrorMatches(error -> {
                    assertThat(error).isInstanceOf(RuntimeException.class);
                    assertThat(error.getMessage()).isEqualTo("Error en mapeo");
                    return true;
                })
                .verify();
    }
}
