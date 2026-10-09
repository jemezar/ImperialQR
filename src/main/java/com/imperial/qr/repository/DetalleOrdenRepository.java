package com.imperial.qr.repository;

import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.domain.enums.EstadoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    @Query("SELECT d FROM DetalleOrden d WHERE d.estado IN :estados ORDER BY d.creadoEn ASC")
    List<DetalleOrden> findByEstadoInOrderByCreadoEnAsc(@Param("estados") List<EstadoDetalle> estados);

    @Query("SELECT d FROM DetalleOrden d JOIN FETCH d.orden o LEFT JOIN FETCH o.mesa WHERE d.estado = :estado ORDER BY d.listoEn ASC, d.creadoEn ASC")
    List<DetalleOrden> findByEstadoOrderByListoEnAsc(@Param("estado") EstadoDetalle estado);

    @Query("SELECT d.plato.nombre, SUM(d.cantidad) FROM DetalleOrden d " +
           "WHERE d.creadoEn >= :desde AND d.creadoEn <= :hasta AND d.estado <> 'CANCELADO' " +
           "GROUP BY d.plato.nombre ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> platosMasVendidos(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
