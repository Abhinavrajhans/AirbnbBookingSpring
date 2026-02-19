package com.example.AirbnbBookingSpring.saga;


import io.netty.util.Timeout;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SagaEventConsumer {

    private static final String SAGA_QUEUE = "saga:events"; // TODO : make this configurable
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedDelay = 500) //poll every 500 mili seconds
    public void consumeEvents(){
        try {


            String eventJson = redisTemplate.opsForList().leftPop(SAGA_QUEUE, 1, TimeUnit.SECONDS);
            if (eventJson != null && !eventJson.isEmpty()) {

            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
