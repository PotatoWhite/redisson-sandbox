package me.potato;

import me.potato.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

public class Lec06MapTest extends BaseTest {

    @Test
    public void mapTest() {
        var map  = client.getMap("user:1", StringCodec.INSTANCE);
        var name = map.put("name", "sam");
        var age  = map.put("age", "10");
        var city = map.put("city", "seoul");

        StepVerifier.create(name.concatWith(age).concatWith(city))
                .verifyComplete();

    }

    @Test
    public void mapTest2() {
        var map = client.getMap("user:2", StringCodec.INSTANCE);
        var javaMap = Map.of(
                "name", "sam",
                "age", "10",
                "city", "seoul"
        );

        StepVerifier.create(map.putAll(javaMap).then())
                .verifyComplete();

    }

    @Test
    public void mapTest3() {
        // Map<Integer, Student>
        var intStudentCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        var map             = client.getMap("users", intStudentCodec);
        var student1        = new Student("sam", 10, "seoul", List.of(1, 2, 3));
        var student2        = new Student("jake", 30, "incheon", List.of(4, 5, 6));
        var student3        = new Student("mike", 20, "busan", List.of(7, 8, 9));

        var mono1 = map.put(1, student1);
        var mono2 = map.put(2, student2);
        var mono3 = map.put(3, student3);

        StepVerifier.create(mono1.concatWith(mono2).concatWith(mono3).then())
                .verifyComplete();

    }
}
