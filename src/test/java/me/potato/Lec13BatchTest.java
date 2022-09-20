package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashSet;

public class Lec13BatchTest extends BaseTest {

    // 통신 한번 2초 미만
    @Test
    public void batchTest() {
        var batch = client.createBatch(BatchOptions.defaults());
        var list  = batch.getList("numbers-list", IntegerCodec.INSTANCE);
        var set   = batch.getSet("numbers-set", IntegerCodec.INSTANCE);

        var _list = new ArrayList<Integer>();
        var _set  = new HashSet<Integer>();

        for (int i = 0; i < 500_000; i++) {
            _list.add(i);
            _set.add(i);
        }

        list.addAll(_list);
        set.addAll(_set);

        StepVerifier.create(batch.execute().then())
                .verifyComplete();

    }


    // 통신 백만번 40초 이
    @Test
    public void regularTest() {
        RListReactive<Long> list  = client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long>  set   = client.getSet("numbers-set", LongCodec.INSTANCE);

        var mono = Flux.range(1, 500_000)
                .map(Long::valueOf)
                .flatMap(i -> list.add(i).then(set.add(i)))
                .then();


        StepVerifier.create(mono)
                .verifyComplete();

    }
}
