package me.potato;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec14TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1;
    private RBucketReactive<Long> user2;


    // user:1:balance 100
    // user:2:balance 0
    @BeforeAll
    public void accountSetup() {
        user1 = client.getBucket("user:1:balance", LongCodec.INSTANCE);
        user2 = client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> mono = user1.set(100L)
                .then(user2.set(0L))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    public void accountBalanceStatus() {
        var mono = Flux.zip(this.user1.get(), this.user2.get())
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void nonTransactionTest() {
        transfer(user1, user2, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) //  error
                .doOnError(System.out::println)
                .subscribe();

        sleep(1000);
    }

    @Test
    public void transactionTest() {
        var transaction = client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1 = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2 = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        transfer(user1, user2, 50)
                .thenReturn(0)
                .then(transaction.commit())
                .doOnError(System.out::println)
                .onErrorResume(e -> transaction.rollback())
                .subscribe();

        sleep(1000);
    }


    private Mono<Void> transfer(RBucketReactive<Long> from, RBucketReactive<Long> to, long amount) {
        return Flux.zip(from.get(), to.get())
                .filter(tuple -> tuple.getT1() >= amount) // min balance check
                .flatMap(tuple -> from.set(tuple.getT1() - amount).thenReturn(tuple))
                .flatMap(tuple -> to.set(tuple.getT2() + amount))
                .then();
    }


}
