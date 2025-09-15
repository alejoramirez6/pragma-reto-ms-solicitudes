package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.repository;

import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.EstadoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la gestión de estados de solicitud.
 */
@Repository
public interface IEstadoRepository extends R2dbcRepository<EstadoEntity, Long> {
    
    /**
     * Busca un estado por su nombre.
     * @param nombre El nombre del estado
     * @return Mono con la entidad del estado encontrada
     */
    Mono<EstadoEntity> findByNombre(String nombre);
}
