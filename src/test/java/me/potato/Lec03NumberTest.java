package me.potato;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec03NumberTest extends BaseTest {

    @Test
    public void keyValueIncrementTest() {
        // set k v --> incr v, decr v
        var bucket = this.client.getAtomicLong("user:1:visit");
        bucket.set(0).subscribe();
        var mono = Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(i -> bucket.incrementAndGet())
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
