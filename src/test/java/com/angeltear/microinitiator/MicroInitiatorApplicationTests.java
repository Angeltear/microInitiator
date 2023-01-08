package com.angeltear.microinitiator;

import com.angeltear.microinitiator.Config.RedisConfig;
import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MicroInitiatorApplicationTests {

    @Autowired
    RedisConfig redisConfig;
    @Test
    void contextLoads() {
    }

    @Test
    void testClientCreation(){
        RedisClient redisClient = redisConfig.getClient();
        assert redisClient!=null;
    }
}
