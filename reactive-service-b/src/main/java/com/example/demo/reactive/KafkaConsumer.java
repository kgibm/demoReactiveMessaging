package com.example.demo.reactive;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaConsumer {
    @Incoming("prices1")
    public void consume1(int price) {
        System.out.println("KafkaConsumer.consume1 received price " + price + " @ " + java.time.Instant.now());
    }

    @Incoming("prices2")
    public void consume2(int price) {
        System.out.println("KafkaConsumer.consume2 received price " + price + " @ " + java.time.Instant.now());
    }
}
