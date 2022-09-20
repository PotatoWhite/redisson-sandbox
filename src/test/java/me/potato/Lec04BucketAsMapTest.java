package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.test.StepVerifier;

public class Lec04BucketAsMapTest extends BaseTest {
    // user:1:name
    // user:2:name
    // user:3:name

    @Test
    void bucketAsMapTest() {
        var mono = client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name")
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

}
