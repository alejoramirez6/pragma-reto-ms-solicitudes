package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.repository;

import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.TipoPrestamoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la gestión de tipos de préstamo.
 */
@Repository
public interface ITipoPrestamoRepository extends R2dbcRepository<TipoPrestamoEntity, Long> {
    
    /**
     * Busca un tipo de préstamo por su nombre.
     * @param nombre El nombre del tipo de préstamo
     * @return Mono con la entidad del tipo de préstamo encontrada
     */
    Mono<TipoPrestamoEntity> findByNombre(String nombre);
}
