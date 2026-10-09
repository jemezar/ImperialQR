package com.imperial.qr.repository;

import com.imperial.qr.domain.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByCodigoQr(String codigoQr);
    Optional<Mesa> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
}
