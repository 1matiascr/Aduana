package com.frontintel.aduanas.ms_operaciones.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionesController {

    // Simula la revisión aduanera de un vehículo
    // GET http://localhost:8081/api/operaciones/validar/AB-123-CD
    @GetMapping("/validar/{patente}")
    public Map<String, Object> validarVehiculo(@PathVariable String patente) {
        // Simulamos un chequeo aleatorio del SAG / Aduana chilena
        String[] estados = {"APROBADO", "RECHAZADO - PAPELES INCOMPLETOS", "RETENIDO - REVISIÓN DE CARGA"};
        int index = new Random().nextInt(estados.length);
        String resultado = estados[index];

        return Map.of(
            "patente", patente,
            "controlAduanero", resultado,
            "permisoParaCruzar", resultado.equals("APROBADO")
        );
    }
}