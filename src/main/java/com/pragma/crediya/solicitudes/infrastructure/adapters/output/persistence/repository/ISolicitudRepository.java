package com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.repository;

import com.pragma.crediya.solicitudes.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends ReactiveCrudRepository<SolicitudEntity, Long> {
}
