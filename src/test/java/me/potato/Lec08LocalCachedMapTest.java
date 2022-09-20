package me.potato;

import me.potato.config.RedissonConfig;
import me.potato.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class Lec08LocalCachedMapTest extends BaseTest {

    private RLocalCachedMap studentMap;

    @BeforeAll
    public void setupClient() {
        var config = new RedissonConfig();
        var client = config.getClient();

        var mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);


        studentMap = client.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions
        );
    }

    @Test
    public void appServer1() {
        var student1 = new Student("sam", 10, "seoul", List.of(1, 2, 3));
        var student2 = new Student("jake", 30, "incheon", List.of(4, 5, 6));

        studentMap.put(1, student1);
        studentMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i + " ==>" + studentMap.get(1)))
                .subscribe();

        sleep(60000);
    }

    @Test
    public void appServer2() {
        var student1 = new Student("sam-updated", 10, "seoul", List.of(1, 2, 3));
        studentMap.put(1, student1);

    }
}
