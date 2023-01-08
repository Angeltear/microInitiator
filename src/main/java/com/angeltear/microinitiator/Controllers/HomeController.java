package com.angeltear.microinitiator.Controllers;

import com.angeltear.microinitiator.Config.RedisConfig;
import com.angeltear.microinitiator.Model.PaymentRequest;
import com.angeltear.microinitiator.Serializer.PaymentRequestSerializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private RedisConfig redisConfig;

    @GetMapping("/")
    public String helloRedis() {
        RedisClient redisClient = redisConfig.getClient();

        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(new ByteArrayCodec());
        RedisCommands<byte[], byte[]> syncCommands = connection.sync();
        PaymentRequestSerializer serializer = new PaymentRequestSerializer();

        PaymentRequest request = new PaymentRequest();
         request.setClientId(123);
         request.setPaymentId(1);
         request.setPaymentSum(354.43);

        syncCommands.lpush("appQueue".getBytes(), serializer.encode(request));

        Long element = syncCommands.llen("appQueue".getBytes());

        connection.close();
        return element.toString();
    }

}
