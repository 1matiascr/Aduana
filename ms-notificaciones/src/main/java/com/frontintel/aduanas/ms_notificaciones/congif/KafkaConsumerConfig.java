package com.frontintel.aduanas.ms_notificaciones.congif;

import com.frontintel.aduanas.ms_notificaciones.dtos.EventoCruceDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Define la fábrica del consumidor, especificando cómo se conecta a los servidores
     * y qué deserializadores utilizará para desencriptar el mensaje de la red.
     */
    @Bean
    public ConsumerFactory<String, EventoCruceDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Configuramos el deserializador de JSON específicamente para nuestro DTO
        JsonDeserializer<EventoCruceDto> jsonDeserializer = new JsonDeserializer<>(EventoCruceDto.class);
        jsonDeserializer.addTrustedPackages("com.frontintel.aduanas.*");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props, 
                new StringDeserializer(), 
                jsonDeserializer
        );
    }

    /**
     * Permite que la anotación @KafkaListener en las capas de servicio funcione
     * de forma multihilo (concurrente), ideal para procesar ráfagas de trámites.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventoCruceDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EventoCruceDto> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}