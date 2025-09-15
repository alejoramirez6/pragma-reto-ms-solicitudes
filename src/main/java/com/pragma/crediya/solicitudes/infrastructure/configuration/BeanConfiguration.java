package com.pragma.crediya.solicitudes.infrastructure.configuration;

import com.pragma.crediya.solicitudes.domain.ports.out.ISolicitudRepositoryPort;
import com.pragma.crediya.solicitudes.domain.ports.out.UserClientPort;
import com.pragma.crediya.solicitudes.domain.usecase.SolicitudUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class BeanConfiguration {

    @Bean
    public SolicitudUseCase solicitudUseCase(ISolicitudRepositoryPort solicitudRepositoryPort,
                                           UserClientPort userClientPort,
                                           TransactionalOperator transactionalOperator) {
        return new SolicitudUseCase(solicitudRepositoryPort, userClientPort, transactionalOperator);
    }
}
