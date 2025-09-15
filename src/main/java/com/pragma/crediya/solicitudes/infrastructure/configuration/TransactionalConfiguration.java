package com.pragma.crediya.solicitudes.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.ReactiveTransactionManager;

/**
 * Configuración para transacciones reactivas.
 * Esta clase configura el TransactionalOperator necesario para manejar
 * transacciones en operaciones reactivas de WebFlux.
 * 
 * @author Pragma - Equipo Desarrollo
 * @version 1.0
 * @since 2024
 */
@Configuration
public class TransactionalConfiguration {

    /**
     * Crea y configura el bean TransactionalOperator.
     * Este operador permite manejar transacciones de forma reactiva
     * usando el patrón .as(transactionalOperator::transactional).
     * 
     * @param transactionManager El administrador de transacciones reactivo
     * @return TransactionalOperator configurado
     */
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
