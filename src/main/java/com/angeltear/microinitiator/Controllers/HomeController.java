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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @Autowired
    private RedisConfig redisConfig;

    @Value("${requestPerClient}")
    private int allowedSimultaneousPerClient;

    @PostMapping("/")
    public String helloRedis(@RequestBody PaymentRequest request) {
        RedisClient redisClient = redisConfig.getClient();
        log.info("Incoming request: " + request.toString());
        //Open a connection to the redis client submitting and accepting byte arrays instead of strings, since we're using custom complex objects
        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(new ByteArrayCodec());
        RedisCommands<byte[], byte[]> syncCommands = connection.sync();

        PaymentRequestSerializer serializer = new PaymentRequestSerializer();

        //Get current unprocessed requests for customer by checking customer's personal list (with id as a key for the list)
        Long currentAttempts = syncCommands.llen(Long.toString(request.getClientId()).getBytes());
        log.info("Current attempts for customer " + request.getClientId() +  " being processed : " + currentAttempts + ". Max allowance: " + allowedSimultaneousPerClient);

        //If there are more unprocessed requests than the allowed, decline the request to prevent abuse.
        if (currentAttempts > allowedSimultaneousPerClient){
            log.info("Request rejected - too many unprocessed events for customer.");
            connection.close();
            return "Nop";
        }
        //If the client has less unprocessed requests than the allowed ones, push the whole request to the main queue and the client ID to his individual queue
        else {
            syncCommands.lpush("appQueue".getBytes(), serializer.encode(request));
            log.info("Added request to main queue.");
            syncCommands.lpush(Long.toString(request.getClientId()).getBytes(), "paymentAttempt".getBytes());
            log.info("Added customerID to individual queue.");
            Long element = syncCommands.llen("appQueue".getBytes()); //TODO - to be deleted in the cleanup

            connection.close();
            return element.toString();
        }
    }

}
