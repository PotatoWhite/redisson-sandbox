package me.potato;

import me.potato.assignment.Category;
import me.potato.assignment.PriorityQueue;
import me.potato.assignment.UserOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec16PriorityQueueTest extends BaseTest {

    private PriorityQueue priorityQueue;


    @BeforeAll
    public void setupQueue() {
        RScoredSortedSetReactive<UserOrder> sortedSet = client.getScoredSortedSet("user:order", new TypedJsonJacksonCodec(UserOrder.class));
        this.priorityQueue = new PriorityQueue(sortedSet);
    }

    @Test
    public void producer1() {
        var u1 = new UserOrder(1, Category.GUEST);
        var u2 = new UserOrder(2, Category.STD);
        var u3 = new UserOrder(3, Category.PRIME);
        var u4 = new UserOrder(4, Category.STD);
        var u5 = new UserOrder(5, Category.GUEST);

        var mono = Flux.just(u1, u2, u3, u4, u5)
                .flatMap(this.priorityQueue::add)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void producer2() {
        Flux.interval(Duration.ofSeconds(1))
                .map(l -> (l.intValue() * 5))
                .doOnNext(i -> {
                    var u1 = new UserOrder(i + 1, Category.GUEST);
                    var u2 = new UserOrder(i + 2, Category.STD);
                    var u3 = new UserOrder(i + 3, Category.PRIME);
                    var u4 = new UserOrder(i + 4, Category.STD);
                    var u5 = new UserOrder(i + 5, Category.GUEST);

                    var mono = Flux.just(u1, u2, u3, u4, u5)
                            .flatMap(this.priorityQueue::add)
                            .then();

                    StepVerifier.create(mono)
                            .verifyComplete();
                }).subscribe();


        sleep(60_000);

    }

    @Test
    public void consumer() {
        var flux = this.priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(System.out::println)
                .subscribe();

        sleep(600_000);
    }

}
