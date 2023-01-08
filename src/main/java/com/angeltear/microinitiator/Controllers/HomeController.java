package com.angeltear.microinitiator.Controllers;

import com.angeltear.microinitiator.Config.RedisConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private RedisConfig redisConfig;

    @GetMapping("/")
    public String helloRedis() {
        RedisClient redisClient = redisConfig.getClient();

        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();


        syncCommands.set("key", "Hello from Redis!");

        syncCommands.lpush("appQueue", "firstElement");
        syncCommands.lpush("appQueue", "secondelementElement");

        Long element = syncCommands.llen("appQueue");

        connection.close();
        return element.toString();
    }

}
