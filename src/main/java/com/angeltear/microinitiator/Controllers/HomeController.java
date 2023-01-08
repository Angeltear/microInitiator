package com.angeltear.microinitiator.Controllers;

import com.angeltear.microinitiator.Config.RedisConfig;
import com.angeltear.microinitiator.Model.PaymentRequest;
import com.angeltear.microinitiator.Serializer.PaymentRequestSerializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @Autowired
    private RedisConfig redisConfig;

    @PostMapping("/")
    public String helloRedis(@RequestBody PaymentRequest request) {
        RedisClient redisClient = redisConfig.getClient();
        log.info(request.toString());
        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(new ByteArrayCodec());
        RedisCommands<byte[], byte[]> syncCommands = connection.sync();
        PaymentRequestSerializer serializer = new PaymentRequestSerializer();


        syncCommands.lpush("appQueue".getBytes(), serializer.encode(request));

        Long element = syncCommands.llen("appQueue".getBytes());

        connection.close();
        return element.toString();
    }

}
