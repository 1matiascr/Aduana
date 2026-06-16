package com.frontintel.aduanas.ms_notificaciones.services;

import com.frontintel.aduanas.ms_notificaciones.models.Turno;
import com.frontintel.aduanas.ms_notificaciones.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilaVirtualService {

    private final TurnoRepository turnoRepository;

    /**
     * Registra un nuevo vehículo en la cola virtual en memoria RAM (Redis).
     */
    public void asignarTurnoAFila(String rutViajero, String idTramite, String patenteVehiculo) {
        // Calcular de manera secuencial el número de turno basándonos en los que ya están en espera
        long turnosActivos = turnoRepository.countByEstadoTurno("EN_ESPERA");
        long nuevoNumeroTurno = turnosActivos + 1;

        Turno nuevoTurno = Turno.builder()
                .rutViajero(rutViajero)
                .idTramite(idTramite)
                .patenteVehiculo(patenteVehiculo)
                .numeroTurno(nuevoNumeroTurno)
                .fechaHoraIngreso(LocalDateTime.now())
                .estadoTurno("EN_ESPERA")
                .build();

        turnoRepository.save(nuevoTurno);
        log.info("Turno #{} asignado exitosamente en Redis para el RUT: {}", nuevoNumeroTurno, rutViajero);
    }

    /**
     * Calcula dinámicamente cuántos vehículos tiene por delante un usuario en la cola.
     */
    public long calcularVehiculosAdelante(String rutViajero) {
        return turnoRepository.findById(rutViajero)
                .map(turnoUsuario -> {
                    if (!"EN_ESPERA".equals(turnoUsuario.getEstadoTurno())) {
                        return 0L; // Si ya fue atendido o expiró, no tiene a nadie adelante
                    }
                    
                    // Traemos los turnos en espera y contamos cuántos tienen un número menor
                    List<Turno> todosEnEspera = turnoRepository.findByEstadoTurno("EN_ESPERA");
                    return todosEnEspera.stream()
                            .filter(t -> t.getNumeroTurno() < turnoUsuario.getNumeroTurno())
                            .count();
                })
                .orElse(0L);
    }

    /**
     * Obtiene el turno completo de un viajero por su ID (RUT).
     */
    public Turno obtenerTurnoPorRut(String rutViajero) {
        return turnoRepository.findById(rutViajero).orElse(null);
    }
}