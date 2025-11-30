package com.renault.garage.infrastructure.event;

import com.renault.garage.domain.event.VehiculeCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class VehiculeKafkaConsumerTest {

    @Test
    void onVehiculeCreated_acknowledgesMessage() {
        VehiculeKafkaConsumer consumer = new VehiculeKafkaConsumer();
        VehiculeCreatedEvent event = new VehiculeCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "RENAULT", 2024, "ESSENCE"
        );

        Acknowledgment ack = Mockito.mock(Acknowledgment.class);

        assertDoesNotThrow(() -> consumer.onVehiculeCreated(event, 0, 0L, ack));
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }
}
