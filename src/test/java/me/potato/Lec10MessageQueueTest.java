package me.potato;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec10MessageQueueTest extends BaseTest {

    private RBlockingQueueReactive<Object> msgQueue;

    @BeforeAll
    public void setupQueue() {
        msgQueue = client.getBlockingQueue("message-queue", LongCodec.INSTANCE);
    }

    // Consumer 1
    @Test
    public void consumer1() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("consumer 1: " + i))
                .doOnError(e -> System.out.println("error 1: " + e.getMessage()))
                .subscribe();
        sleep(600_000);
    }

    // Consumer 2
    @Test
    public void consumer2() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("consumer 2: " + i))
                .doOnError(e -> System.out.println("error 2: " + e.getMessage()))
                .subscribe();
        sleep(600_000);
    }

    @Test
    public void producer() {
        var mono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("producer add: " + i))
                .flatMap(i -> msgQueue.add(Long.valueOf(i)))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();

    }


}
