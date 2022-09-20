package me.potato;

import org.junit.jupiter.api.Test;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

public class Lec12PubSub extends BaseTest {

    @Test
    public void subscriber1() {
        var topic = client.getTopic("slack-room1", StringCodec.INSTANCE);

        topic.getMessages(String.class)
                .doOnError(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();

        sleep(600_000);
    }

    @Test
    public void subscriber2() {
        // pattern : start with slack-room
        var patternTopic = client.getPatternTopic("slack-room*", StringCodec.INSTANCE);

        patternTopic.addListener(String.class, new PatternMessageListener<String>() {
            @Override
            public void onMessage(CharSequence pattern, CharSequence channel, String msg) {
                System.out.println(pattern + " : " + channel + " : " + msg);
            }
        }).subscribe();
        sleep(600_000);
    }

}
