package com.imperial.qr.repository;

import com.imperial.qr.domain.model.Domicilio;
import com.imperial.qr.domain.enums.EstadoDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {
    Optional<Domicilio> findByOrdenId(Long ordenId);
    List<Domicilio> findByEstado(EstadoDomicilio estado);
    List<Domicilio> findByDomiciliarioId(Long domiciliarioId);
}
