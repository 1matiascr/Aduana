package com.frontintel.aduanas.ms_notificaciones.repository;

import com.frontintel.aduanas.ms_notificaciones.models.Turno;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de la entidad Turno en Redis.
 * Al heredar de CrudRepository, Spring Data Redis genera automáticamente
 * las operaciones básicas (save, findById, delete, etc.) en memoria RAM.
 */
@Repository
public interface TurnoRepository extends CrudRepository<Turno, String> {

    /**
     * Busca todos los turnos que tengan un estado específico (ej: "EN_ESPERA").
     * Esto es posible gracias al @Indexed que colocamos en el modelo.
     * * @param estadoTurno Estado por el cual filtrar.
     * @return Lista de turnos encontrados.
     */
    List<Turno> findByEstadoTurno(String estadoTurno);

    /**
     * Busca un turno específico a través de la patente del vehículo.
     * Útil para los controles automáticos de barrera o portones físicos.
     * * @param patenteVehiculo Patente/Placa del auto.
     * @return Un contenedor Optional con el Turno si existe.
     */
    Optional<Turno> findByPatenteVehiculo(String patenteVehiculo);

    /**
     * Cuenta cuántos vehículos están actualmente en un estado específico.
     * Ideal para calcular métricas en tiempo real de congestión en la frontera.
     * * @param estadoTurno Estado a consultar (ej: "EN_ESPERA").
     * @return Cantidad de turnos activos.
     */
    long countByEstadoTurno(String estadoTurno);
}