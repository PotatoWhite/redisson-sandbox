package me.potato;

import me.potato.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.test.StepVerifier;

import java.util.List;

public class Lec02KeyValueObjectTest extends BaseTest {

    @Test
    void keyValueObjectTest() {
        var student = new Student("sam", 10, "seoul", List.of(1, 2, 3));
        var bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        var set = bucket.set(student);
        var get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }
}
