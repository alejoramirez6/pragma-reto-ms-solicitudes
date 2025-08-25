package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.repository;

import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUsuarioRepository extends ReactiveCrudRepository<UsuarioEntity, Long> {
    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);
}
