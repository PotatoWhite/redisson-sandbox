package me.potato.assignment;

import org.redisson.api.RScoredSortedSetReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PriorityQueue {
    private RScoredSortedSetReactive<UserOrder> queue;

    public PriorityQueue(RScoredSortedSetReactive<UserOrder> queue) {
        this.queue = queue;
    }

    public Mono<Void> add(UserOrder item) {
        return queue.add(getScore(item.getCategory()), item).then();
    }

    public Flux<UserOrder> takeItems() {
        return queue.takeFirstElements()
                .limitRate(1);
    }

    public double getScore(Category category) {
        return category.ordinal() + Double.parseDouble("0." + System.nanoTime());
    }
}
