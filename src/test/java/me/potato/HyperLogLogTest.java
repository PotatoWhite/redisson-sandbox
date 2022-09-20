package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class HyperLogLogTest extends BaseTest {
    @Test // 12.5 kb
    public void count() {
        RHyperLogLogReactive<Long> counter = client.getHyperLogLog("user:visits", LongCodec.INSTANCE);
        var list1 = LongStream.rangeClosed(1, 10000)
                .boxed()
                .collect(Collectors.toList());

        var list2 = LongStream.rangeClosed(10001, 20000)
                .boxed()
                .collect(Collectors.toList());

        var list3 = LongStream.rangeClosed(20001, 30000)
                .boxed()
                .collect(Collectors.toList());

        var list4 = LongStream.rangeClosed(30001, 40000)
                .boxed()
                .collect(Collectors.toList());

        var mono = Flux.just(list1, list2, list3, list4)
                .flatMap(counter::addAll)
                .then();


        StepVerifier.create(mono)
                .verifyComplete();

        counter.count()
                .doOnNext(System.out::println)
                .subscribe();



    }
}
