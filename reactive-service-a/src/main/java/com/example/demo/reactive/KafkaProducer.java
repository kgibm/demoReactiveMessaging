package com.example.demo.reactive;

import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;

@ApplicationScoped
public class KafkaProducer {
    Random random = new Random();
    
    @Outgoing("prices")
    public Flowable<Integer> generatePrice() {
        System.out.println("KafkaProducer.generatePrice initializing @ " + java.time.Instant.now());
        return Flowable.interval(30, TimeUnit.SECONDS)
            .map(tick ->
                {
                    int price = random.nextInt(1000);
                    System.out.println("KafkaProducer generating price: " + price + " @ " + java.time.Instant.now());
                    return price;
                });
    }
}
