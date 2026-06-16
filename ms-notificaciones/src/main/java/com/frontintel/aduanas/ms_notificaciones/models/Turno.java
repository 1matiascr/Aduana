package com.frontintel.aduanas.ms_notificaciones.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa la entidad de un Turno en la Fila Virtual.
 * Al utilizar @RedisHash, Spring Boot guardará automáticamente 
 * este objeto en la base de datos Redis en memoria RAM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("TurnoFilaVirtual")
public class Turno implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * El ID único del turno en Redis. 
     * Usaremos el rut/pasaporte del viajero como ID para asegurar que 
     * un viajero no pueda tomar más de un turno de forma simultánea.
     */
    @Id
    private String rutViajero;

    /**
     * Código identificador del trámite aduanero asociado 
     * (generado previamente por ms-operaciones).
     */
    private String idTramite;

    /**
     * La patente o placa del vehículo para el control en los portones.
     */
    private String patenteVehiculo;

    /**
     * El número de posición numérico en la fila de atención.
     * La anotación @Indexed permite que Spring Data Redis pueda realizar 
     * búsquedas y filtros rápidos por este campo en memoria.
     */
    @Indexed
    private Long numeroTurno;

    /**
     * Timestamp exacto en el que el viajero ingresó a la fila virtual 
     * (al momento de confirmarse su pre-registro).
     */
    private LocalDateTime fechaHoraIngreso;

    /**
     * Estado actual del turno dentro del flujo de la fila.
     * Valores posibles: "EN_ESPERA", "LLAMADO_A_VENTANILLA", "PROCESADO", "EXPIRADO"
     */
    @Indexed
    private String estadoTurno;
}