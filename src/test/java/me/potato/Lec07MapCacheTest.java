package me.potato;

import me.potato.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lec07MapCacheTest extends BaseTest {
    @Test
    void mapCacheTest() {
        var intStudentCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        var mapCache        = client.getMapCache("users:cache", intStudentCodec);

        var student1 = new Student("sam", 10, "seoul", List.of(1, 2, 3));
        var student2 = new Student("jake", 30, "incheon", List.of(4, 5, 6));
        var student3 = new Student("mike", 20, "busan", List.of(7, 8, 9));

        var mono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        var mono2 = mapCache.put(2, student2, 5, TimeUnit.SECONDS);
        var mono3 = mapCache.put(3, student3, 5, TimeUnit.SECONDS);

        StepVerifier.create(mono1.concatWith(mono2).concatWith(mono3).then())
                .verifyComplete();

        sleep(3000);

        mapCache.get(1)
                .doOnNext(System.out::println)
                .subscribe();

        mapCache.get(2)
                .doOnNext(System.out::println)
                .subscribe();

        mapCache.get(3)
                .doOnNext(System.out::println)
                .subscribe();

        sleep(3000);

    }
}
