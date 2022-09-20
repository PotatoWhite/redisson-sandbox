package me.potato;

import me.potato.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonReactiveClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    private final RedissonConfig         redissonConfig = new RedissonConfig();
    protected     RedissonReactiveClient client;

    @BeforeAll
    public void setup() {
        client = redissonConfig.getReactiveClient();
    }

    @AfterAll
    public void shutdown() {
        client.shutdown();
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {     }
    }
}
