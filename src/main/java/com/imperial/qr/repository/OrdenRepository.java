package com.imperial.qr.repository;

import com.imperial.qr.domain.model.Mesa;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.TipoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByMesaAndEstado(Mesa mesa, EstadoOrden estado);

    @Query("SELECT o FROM Orden o WHERE " +
           "(:estado IS NULL OR o.estado = :estado) AND " +
           "(:mesaId IS NULL OR (o.mesa IS NOT NULL AND o.mesa.id = :mesaId)) AND " +
           "(:tipo IS NULL OR o.tipo = :tipo) " +
           "ORDER BY o.apertura DESC")
    List<Orden> buscarConFiltros(@Param("estado") EstadoOrden estado,
                                 @Param("mesaId") Long mesaId,
                                 @Param("tipo") TipoOrden tipo);

    @Query("SELECT o FROM Orden o WHERE o.apertura >= :desde AND o.apertura <= :hasta AND o.estado = :estado")
    List<Orden> findByFechaRangoAndEstado(@Param("desde") LocalDateTime desde,
                                          @Param("hasta") LocalDateTime hasta,
                                          @Param("estado") EstadoOrden estado);
}
