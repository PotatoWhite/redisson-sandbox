package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.client.codec.StringCodec;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

public class Lec05EventListenerTest extends BaseTest {

    @Test
    public void expiredEventTest() {
        var bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        var set    = bucket.set("sam", 10, TimeUnit.SECONDS);
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        var event = bucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String name) {
                System.out.println("Expired: " + name);
            }
        }).then();

        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();

        sleep(15000);
    }


    @Test
    public void deletedEventTest() {
        var bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        var set    = bucket.set("sam");
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        var event = bucket.addListener(new DeletedObjectListener() {
            @Override
            public void onDeleted(String name) {
                System.out.println("Deleted: " + name);
            }
        }).then();

        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();




        // for manual delete
        sleep(60000);
    }
}
