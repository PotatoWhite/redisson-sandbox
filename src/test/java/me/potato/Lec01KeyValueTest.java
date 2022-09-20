package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

public class Lec01KeyValueTest extends BaseTest {
    @Test
    public void keyValueAccessTest() throws InterruptedException {
        var bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        var set    = bucket.set("sam");
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    public void keyValueExpiryTest() throws InterruptedException {
        var bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        var set    = bucket.set("sam", 10, TimeUnit.SECONDS);
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();

    }

    @Test
    public void keyValueExtendExpiryTest() throws InterruptedException {
        var bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        var set    = bucket.set("sam", 10, TimeUnit.SECONDS);
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();

        sleep(5000);


        //extend
        var extend = bucket.expire(60, TimeUnit.SECONDS);
        StepVerifier.create(extend)
                .expectNext(true)
                .verifyComplete();

        // access expire time
        var ttl = bucket.remainTimeToLive()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(ttl)
                .verifyComplete();
    }


}
