package com.example.AirbnbBookingSpring.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaEventPublisher {

    public static final String SAGA_QUEUE = "saga:events";
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvent(String eventType, String step, Map<String,Object> payload){
        String sagaId= UUID.randomUUID().toString();
        SagaEvent sagaEvent = SagaEvent.builder()
                .sagaId(sagaId)
                .eventType(eventType)
                .step(step)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .status(SagaStatus.PENDING)
                .build();
        try{
            String eventJson = objectMapper.writeValueAsString(sagaEvent);
            redisTemplate.opsForList().rightPush(SAGA_QUEUE,eventJson);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to publish the sagae Event",e);
        }

    }
}
