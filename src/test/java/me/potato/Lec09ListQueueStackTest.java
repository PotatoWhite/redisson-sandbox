package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.LongCodec;
import reactor.test.StepVerifier;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Lec09ListQueueStackTest extends BaseTest {

    @Test
    public void listTest() {
        // lrange number-input 0 -1
        var list = client.getList("number-input", LongCodec.INSTANCE);
        list.delete().subscribe();

        var longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNextMatches(size -> size % 10 == 0)
                .verifyComplete();

    }

    @Test
    public void queueTest() {
        // lrange number-input 0 -1
        var queue = client.getQueue("number-input", LongCodec.INSTANCE);

        var queuePoll = queue.poll()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(queuePoll)
                .verifyComplete();

        StepVerifier.create(queue.size())
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    public void stackTest() { // Deque
        var list = client.getList("number-input", LongCodec.INSTANCE);
        list.delete().subscribe();
        listTest();

        // lrange number-input 0 -1
        var deque = client.getDeque("number-input", LongCodec.INSTANCE);


        var mono = deque.pollLast()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();


        StepVerifier.create(mono)
                .verifyComplete();

        StepVerifier.create(deque.size())
                .expectNext(2)
                .verifyComplete();
    }
}
