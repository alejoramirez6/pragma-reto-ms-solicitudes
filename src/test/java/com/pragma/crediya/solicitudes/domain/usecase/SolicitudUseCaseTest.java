package com.pragma.crediya.solicitudes.domain.usecase;

import com.pragma.crediya.solicitudes.domain.exception.ClienteNoEncontradoException;
import com.pragma.crediya.solicitudes.domain.model.EstadoSolicitud;
import com.pragma.crediya.solicitudes.domain.model.SolicitudPrestamo;
import com.pragma.crediya.solicitudes.domain.model.TipoPrestamo;
import com.pragma.crediya.solicitudes.domain.ports.out.ISolicitudRepositoryPort;
import com.pragma.crediya.solicitudes.domain.ports.out.UserClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para SolicitudUseCase.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SolicitudUseCase - Tests unitarios")
class SolicitudUseCaseTest {

    @Mock
    private ISolicitudRepositoryPort solicitudRepositoryPort;

    @Mock
    private UserClientPort userClientPort;

    @Mock
    private TransactionalOperator transactionalOperator;

    private SolicitudUseCase solicitudUseCase;
    private SolicitudPrestamo solicitudValida;
    private SolicitudPrestamo solicitudGuardada;
    private UserClientPort.UserInfo userInfoValido;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        solicitudUseCase = new SolicitudUseCase(solicitudRepositoryPort, userClientPort, transactionalOperator);
        
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        solicitudValida = SolicitudPrestamo.builder()
                .documentoIdentidad("12345678")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .build();

        userInfoValido = new UserClientPort.UserInfo(
                1L,
                "Juan",
                "Pérez",
                "juan.perez@email.com",
                "12345678",
                new java.math.BigDecimal("2500000")
        );

        solicitudGuardada = SolicitudPrestamo.builder()
                .id(1L)
                .documentoIdentidad("12345678")
                .email("juan.perez@email.com")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debería crear solicitud exitosamente cuando el cliente existe")
    void deberiaCrearSolicitudExitosamenteCuandoClienteExiste() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("12345678")))
                .thenReturn(Mono.just(userInfoValido));
        when(solicitudRepositoryPort.guardarSolicitud(any(SolicitudPrestamo.class)))
                .thenReturn(Mono.just(solicitudGuardada));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudValida))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.getId()).isEqualTo(1L);
                    assertThat(solicitud.getDocumentoIdentidad()).isEqualTo("12345678");
                    assertThat(solicitud.getMonto()).isEqualTo(new BigDecimal("5000000"));
                    assertThat(solicitud.getPlazo()).isEqualTo(24);
                    assertThat(solicitud.getTipoPrestamo()).isEqualTo(TipoPrestamo.PERSONAL);
                    assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
                    assertThat(solicitud.getFechaCreacion()).isNotNull();
                    assertThat(solicitud.getFechaActualizacion()).isNotNull();
                    return true;
                })
                .verifyComplete();

        verify(userClientPort).obtenerUsuario("12345678");
        verify(solicitudRepositoryPort).guardarSolicitud(any(SolicitudPrestamo.class));
    }

    @Test
    @DisplayName("Debería fallar cuando el cliente no existe")
    void deberiaFallarCuandoClienteNoExiste() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("99999999")))
                .thenReturn(Mono.empty());

        SolicitudPrestamo solicitudClienteInexistente = SolicitudPrestamo.builder()
                .documentoIdentidad("99999999")
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .tipoPrestamo(TipoPrestamo.PERSONAL)
                .build();

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudClienteInexistente))
                .expectErrorMatches(error -> {
                    assertThat(error).isInstanceOf(ClienteNoEncontradoException.class);
                    assertThat(error.getMessage()).contains("No se encontró un cliente registrado");
                    assertThat(error.getMessage()).contains("99999999");
                    return true;
                })
                .verify();

        verify(userClientPort).obtenerUsuario("99999999");
        verify(solicitudRepositoryPort, org.mockito.Mockito.never()).guardarSolicitud(any());
    }

    @Test
    @DisplayName("Debería establecer estado inicial como PENDIENTE_REVISION")
    void deberiaEstablecerEstadoInicialComoPendienteRevision() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("12345678")))
                .thenReturn(Mono.just(userInfoValido));
        when(solicitudRepositoryPort.guardarSolicitud(any(SolicitudPrestamo.class)))
                .thenReturn(Mono.just(solicitudGuardada));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudValida))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería establecer fechas de creación y actualización")
    void deberiaEstablecerFechasDeCreacionYActualizacion() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("12345678")))
                .thenReturn(Mono.just(userInfoValido));
        when(solicitudRepositoryPort.guardarSolicitud(any(SolicitudPrestamo.class)))
                .thenReturn(Mono.just(solicitudGuardada));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudValida))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.getFechaCreacion()).isNotNull();
                    assertThat(solicitud.getFechaActualizacion()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería manejar error en verificación de cliente")
    void deberiaManejarErrorEnVerificacionDeCliente() {
        // Arrange
        when(userClientPort.obtenerUsuario(any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Error de conexión a servicio de usuarios")));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudValida))
                .expectErrorMatches(error -> {
                    assertThat(error).isInstanceOf(RuntimeException.class);
                    assertThat(error.getMessage()).isEqualTo("Error de conexión a servicio de usuarios");
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("Debería manejar error al guardar solicitud")
    void deberiaManejarErrorAlGuardarSolicitud() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("12345678")))
                .thenReturn(Mono.just(userInfoValido));
        when(solicitudRepositoryPort.guardarSolicitud(any(SolicitudPrestamo.class)))
                .thenReturn(Mono.error(new RuntimeException("Error al guardar en BD")));

        // Act & Assert
        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudValida))
                .expectErrorMatches(error -> {
                    assertThat(error).isInstanceOf(RuntimeException.class);
                    assertThat(error.getMessage()).isEqualTo("Error al guardar en BD");
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("Debería procesar diferentes tipos de préstamo")
    void deberiaProcesarDiferentesTiposDePrestamo() {
        // Arrange
        when(userClientPort.obtenerUsuario(eq("12345678")))
                .thenReturn(Mono.just(userInfoValido));

        for (TipoPrestamo tipo : TipoPrestamo.values()) {
            SolicitudPrestamo solicitudTipo = SolicitudPrestamo.builder()
                    .documentoIdentidad("12345678")
                    .monto(new BigDecimal("5000000"))
                    .plazo(24)
                    .tipoPrestamo(tipo)
                    .build();

            SolicitudPrestamo solicitudGuardadaTipo = SolicitudPrestamo.builder()
                    .id(1L)
                    .documentoIdentidad("12345678")
                    .email("juan.perez@email.com")
                    .monto(new BigDecimal("5000000"))
                    .plazo(24)
                    .tipoPrestamo(tipo)
                    .estado(EstadoSolicitud.PENDIENTE_REVISION)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .build();

            when(solicitudRepositoryPort.guardarSolicitud(any(SolicitudPrestamo.class)))
                    .thenReturn(Mono.just(solicitudGuardadaTipo));

            // Act & Assert
            StepVerifier.create(solicitudUseCase.crearSolicitud(solicitudTipo))
                    .expectNextMatches(solicitud -> {
                        assertThat(solicitud.getTipoPrestamo()).isEqualTo(tipo);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Tests para obtenerTodasLasSolicitudes")
    class ObtenerTodasLasSolicitudesTests {

        @Test
        @DisplayName("Debería obtener todas las solicitudes exitosamente")
        void deberiaObtenerTodasLasSolicitudesExitosamente() {
            // Arrange
            SolicitudPrestamo solicitud1 = SolicitudPrestamo.builder()
                    .id(1L)
                    .documentoIdentidad("12345678")
                    .tipoPrestamo(TipoPrestamo.PERSONAL)
                    .monto(BigDecimal.valueOf(5000000))
                    .estado(EstadoSolicitud.PENDIENTE_REVISION)
                    .build();

            SolicitudPrestamo solicitud2 = SolicitudPrestamo.builder()
                    .id(2L)
                    .documentoIdentidad("87654321")
                    .tipoPrestamo(TipoPrestamo.VEHICULAR)
                    .monto(BigDecimal.valueOf(10000000))
                    .estado(EstadoSolicitud.APROBADA)
                    .build();

            when(solicitudRepositoryPort.obtenerTodasLasSolicitudes())
                    .thenReturn(Flux.just(solicitud1, solicitud2));

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerTodasLasSolicitudes())
                    .expectNext(solicitud1)
                    .expectNext(solicitud2)
                    .verifyComplete();

            verify(solicitudRepositoryPort).obtenerTodasLasSolicitudes();
        }

        @Test
        @DisplayName("Debería obtener flujo vacío cuando no hay solicitudes")
        void deberiaObtenerFlujoVacioCuandoNoHaySolicitudes() {
            // Arrange
            when(solicitudRepositoryPort.obtenerTodasLasSolicitudes())
                    .thenReturn(Flux.empty());

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerTodasLasSolicitudes())
                    .verifyComplete();

            verify(solicitudRepositoryPort).obtenerTodasLasSolicitudes();
        }

        @Test
        @DisplayName("Debería manejar errores del repositorio al obtener todas las solicitudes")
        void deberiaManejarErroresDelRepositorio() {
            // Arrange
            RuntimeException error = new RuntimeException("Error de base de datos");
            when(solicitudRepositoryPort.obtenerTodasLasSolicitudes())
                    .thenReturn(Flux.error(error));

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerTodasLasSolicitudes())
                    .expectError(RuntimeException.class)
                    .verify();

            verify(solicitudRepositoryPort).obtenerTodasLasSolicitudes();
        }
    }

    @Nested
    @DisplayName("Tests para obtenerSolicitudesPorCliente")
    class ObtenerSolicitudesPorClienteTests {

        @Test
        @DisplayName("Debería obtener solicitudes por cliente exitosamente")
        void deberiaObtenerSolicitudesPorClienteExitosamente() {
            // Arrange
            String documentoIdentidad = "12345678";
            SolicitudPrestamo solicitud1 = SolicitudPrestamo.builder()
                    .id(1L)
                    .documentoIdentidad(documentoIdentidad)
                    .tipoPrestamo(TipoPrestamo.PERSONAL)
                    .monto(BigDecimal.valueOf(5000000))
                    .estado(EstadoSolicitud.PENDIENTE_REVISION)
                    .build();

            SolicitudPrestamo solicitud2 = SolicitudPrestamo.builder()
                    .id(2L)
                    .documentoIdentidad(documentoIdentidad)
                    .tipoPrestamo(TipoPrestamo.VEHICULAR)
                    .monto(BigDecimal.valueOf(10000000))
                    .estado(EstadoSolicitud.APROBADA)
                    .build();

            when(solicitudRepositoryPort.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .thenReturn(Flux.just(solicitud1, solicitud2));

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .expectNext(solicitud1)
                    .expectNext(solicitud2)
                    .verifyComplete();

            verify(solicitudRepositoryPort).obtenerSolicitudesPorCliente(documentoIdentidad);
        }

        @Test
        @DisplayName("Debería obtener flujo vacío cuando cliente no tiene solicitudes")
        void deberiaObtenerFlujoVacioCuandoClienteNoTieneSolicitudes() {
            // Arrange
            String documentoIdentidad = "99999999";
            when(solicitudRepositoryPort.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .thenReturn(Flux.empty());

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .verifyComplete();

            verify(solicitudRepositoryPort).obtenerSolicitudesPorCliente(documentoIdentidad);
        }

        @Test
        @DisplayName("Debería manejar errores del repositorio al obtener solicitudes por cliente")
        void deberiaManejarErroresDelRepositorioParaCliente() {
            // Arrange
            String documentoIdentidad = "12345678";
            RuntimeException error = new RuntimeException("Error de base de datos");
            when(solicitudRepositoryPort.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .thenReturn(Flux.error(error));

            // Act & Assert
            StepVerifier.create(solicitudUseCase.obtenerSolicitudesPorCliente(documentoIdentidad))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(solicitudRepositoryPort).obtenerSolicitudesPorCliente(documentoIdentidad);
        }
    }
}
