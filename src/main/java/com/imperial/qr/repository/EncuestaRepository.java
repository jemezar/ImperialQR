package com.imperial.qr.repository;

import com.imperial.qr.domain.model.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncuestaRepository extends JpaRepository<Encuesta, Long> {
    Optional<Encuesta> findByOrdenId(Long ordenId);
    boolean existsByOrdenId(Long ordenId);

    @Query("SELECT AVG(e.calComida), AVG(e.calServicio), AVG(e.calGeneral), COUNT(e) FROM Encuesta e")
    Object[] obtenerPromedios();
}
