package com.frontintel.aduanas.ms_notificaciones.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionEmailService {

    private final JavaMailSender mailSender;

    /**
     * Genera y despacha el correo electrónico de confirmación con el ID del trámite.
     */
    public void enviarCorreoConfirmacion(String destino, String idTramite, String patente) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(destino);
            mail.setSubject("Frontera Inteligente - Pre-registro Exitoso");
            mail.setText("Estimado viajero(a),\n\n" +
                    "Le informamos que su trámite de pre-registro aduanero ha sido procesado con éxito.\n\n" +
                    "Detalles de su código:\n" +
                    "• ID de Trámite: " + idTramite + "\n" +
                    "• Vehículo Asociado: " + patente + "\n\n" +
                    "Su turno virtual ya se encuentra activo en memoria. Al aproximarse al Paso Los Libertadores, " +
                    "el sistema validará de forma automatizada sus datos mediante el portón inteligente.\n\n" +
                    "Buen viaje,\n" +
                    "Sistema Binacional de Frontera Inteligente.");

            mailSender.send(mail);
            log.info("Notificación por correo despachada exitosamente hacia: {}", destino);
        } catch (Exception e) {
            log.error("Fallo crítico al despachar correo a {}: {}", destino, e.getMessage());
        }
    }
}
