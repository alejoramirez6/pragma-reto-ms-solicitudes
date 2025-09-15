package com.pragma.crediya.solicitudes.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitarios para las excepciones del dominio.
 */
@DisplayName("Excepciones del dominio - Tests unitarios")
class DomainExceptionsTest {

    @Test
    @DisplayName("ClienteNoEncontradoException debería crearse con mensaje personalizado")
    void clienteNoEncontradoExceptionDeberiaCrerseConMensajePersonalizado() {
        // Arrange
        String mensajeEsperado = "No se encontró cliente con documento: 12345678";

        // ACT
        ClienteNoEncontradoException excepcion = new ClienteNoEncontradoException(mensajeEsperado);

        // ASSERT
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("TipoPrestamoInvalidoException debería crearse con mensaje personalizado")
    void tipoPrestamoInvalidoExceptionDeberiaCrerseConMensajePersonalizado() {
        // ARRANGE
        String mensajeEsperado = "Tipo de préstamo INVALIDO no es válido";

        // ACT
        TipoPrestamoInvalidoException excepcion = new TipoPrestamoInvalidoException(mensajeEsperado);

        // ASSERT
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Las excepciones deberían ser RuntimeException para no requerir try-catch")
    void lasExcepcionesDeberianSerRuntimeExceptionParaNoRequerirTryCatch() {
        assertThrows(ClienteNoEncontradoException.class, () -> {
            throw new ClienteNoEncontradoException("Test");
        });

        assertThrows(TipoPrestamoInvalidoException.class, () -> {
            throw new TipoPrestamoInvalidoException("Test");
        });
    }

    @Test
    @DisplayName("Las excepciones deberían preservar la causa original")
    void lasExcepcionesDeberianPreservarLaCausaOriginal() {
        // Arrange
        RuntimeException causaOriginal = new RuntimeException("Error de BD");
        
        // Act
        ClienteNoEncontradoException excepcion = new ClienteNoEncontradoException("Cliente no encontrado");
        excepcion.initCause(causaOriginal);

        // Assert
        assertThat(excepcion.getCause()).isEqualTo(causaOriginal);
        assertThat(excepcion.getCause().getMessage()).isEqualTo("Error de BD");
    }
}
