package com.frontintel.aduanas.ms_notificaciones.services;

import com.frontintel.aduanas.ms_notificaciones.dtos.EventoCruceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final FilaVirtualService filaVirtualService;
    private final NotificacionEmailService emailService;

    /**
     * Escucha en tiempo real el tópico de trámites de aduanas.
     */
    @KafkaListener(
            topics = "tramites-aduanas-topic",
            groupId = "grupo-notificaciones",
            properties = {"spring.json.value.default.type=com.frontintel.aduanas.notificaciones.dtos.EventoCruceDto"}
    )
    public void procesarEventoCruce(EventoCruceDto evento) {
        log.info("Mensaje asíncrono capturado desde Kafka. Trámite ID: {}", evento.getIdTramite());

        // 1. Ejecutar la regla de negocio para ingresarlo a la fila virtual en Redis
        if ("PRE_REGISTRADO".equals(evento.getEstado())) {
            filaVirtualService.asignarTurnoAFila(
                    evento.getRutViajero(), 
                    evento.getIdTramite(), 
                    evento.getPatenteVehiculo()
            );
            
            // 2. Ejecutar de forma paralela la alerta por correo electrónico
            emailService.enviarCorreoConfirmacion(
                    evento.getCorreoViajero(), 
                    evento.getIdTramite(), 
                    evento.getPatenteVehiculo()
            );
        }
    }
}
