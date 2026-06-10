package com.frontintel.aduanas.ms_notificaciones.controller;

import com.frontintel.aduanas.ms_notificaciones.dtos.TurnoResponseDto;
import com.frontintel.aduanas.ms_notificaciones.models.Turno;
import com.frontintel.aduanas.ms_notificaciones.services.FilaVirtualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/fila")
@RequiredArgsConstructor
public class FilaVirtualController {

    private final FilaVirtualService filaVirtualService;

    /**
     * Endpoint HTTP GET para consultar el estado del turno y autos adelante.
     * Ejemplo de uso: GET http://localhost:8083/api/v1/fila/consultar/12345678-9
     *
     * @param rutViajero El RUT o pasaporte enviado en la URL.
     * @return El DTO con la información formateada para la aplicación del usuario.
     */
    @GetMapping("/consultar/{rutViajero}")
    public ResponseEntity<TurnoResponseDto> consultarEstadoTurno(@PathVariable String rutViajero) {
        log.info("Petición HTTP recibida para consultar turno del RUT: {}", rutViajero);

        // 1. Buscar el turno del viajero en memoria RAM (Redis)
        Turno turno = filaVirtualService.obtenerTurnoPorRut(rutViajero);

        if (turno == null) {
            log.warn("No se encontró ningún turno activo para el RUT: {}", rutViajero);
            return ResponseEntity.notFound().build();
        }

        // 2. Calcular cuántos vehículos tiene por delante de forma dinámica
        long vehiculosAdelante = filaVirtualService.calcularVehiculosAdelante(rutViajero);

        // 3. Mapear los datos de la entidad hacia el DTO de salida de forma limpia
        TurnoResponseDto respuesta = TurnoResponseDto.builder()
                .rutViajero(turno.getRutViajero())
                .numeroTurno(turno.getNumeroTurno())
                .estadoTurno(turno.getEstadoTurno())
                .vehiculosAdelante(vehiculosAdelante)
                .mensajeInformativo("Tránsito expedito en Paso Los Libertadores. Aproxímese con su QR.")
                .build();

        return ResponseEntity.ok(respuesta);
    }
}