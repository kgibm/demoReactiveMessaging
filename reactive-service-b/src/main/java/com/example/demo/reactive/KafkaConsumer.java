package com.example.demo.reactive;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaConsumer {
    @Incoming("prices")
    public void consume(int price) {
        System.out.println("KafkaConsumer.consume received price " + price + " @ " + java.time.Instant.now());
    }
}
