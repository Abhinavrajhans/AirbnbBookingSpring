package com.example.AirbnbBookingSpring.repositories.reads;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.readModels.AirbnbReadModel;
import com.example.AirbnbBookingSpring.models.readModels.AvailabilityReadModel;
import com.example.AirbnbBookingSpring.models.readModels.BookingReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisReadRepository {

    public static final String AIRBNB_KEY_PREFIX = "airbnb:";
    public static final String BOOKING_KEY_PREFIX = "booking:";
    public static final String AVAIABLE_KEY_PREFIX = "avaiable:";

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    public AirbnbReadModel findAirbnbById(Long id){
        String key=AIRBNB_KEY_PREFIX + id;
        String value=redisTemplate.opsForValue().get(key);
        if(value==null){
            return null;
        }
        try{
            return objectMapper.readValue(value, AirbnbReadModel.class);
        }
        catch(JacksonException e){
            throw new RuntimeException("Failed to parse AirbnbReadModel read model from Redis",e);
        }
    }

    public List<AirbnbReadModel> findAllAirbnbs(){
        Set<String> keys=redisTemplate.keys(BOOKING_KEY_PREFIX+"*");
        if(keys.isEmpty()){
            return List.of();
        }
        return keys.stream()
                .map(k->{
                    String value=redisTemplate.opsForValue().get(k);
                    if(value==null){
                        return null;
                    }
                    try{
                        return objectMapper.readValue(value, AirbnbReadModel.class);
                    }
                    catch(JacksonException e){
                        throw new RuntimeException("Failed to parse AirbnbReadModel read model from Redis",e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public BookingReadModel findBookingById(Long id){
        String key=BOOKING_KEY_PREFIX + id;
        String value=redisTemplate.opsForValue().get(key);
        if(value==null){
            return null;
        }
        try{
            return objectMapper.readValue(value, BookingReadModel.class);
        }
        catch(JacksonException e){
            throw new RuntimeException("Failed to parse AirbnbReadModel read model from Redis",e);
        }
    }

    public AvailabilityReadModel findAvailabilityById(Long id){
        String key=AVAIABLE_KEY_PREFIX + id;
        String value=redisTemplate.opsForValue().get(key);
        if(value==null){
            return null;
        }
        try{
            return objectMapper.readValue(value, AvailabilityReadModel.class);
        }
        catch(JacksonException e){
            throw new RuntimeException("Failed to parse AirbnbReadModel read model from Redis",e);
        }
    }

    public BookingReadModel findBookingByIdempotencyKey(String idempotencyKey){
        Set<String>  keys=redisTemplate.keys(BOOKING_KEY_PREFIX+"*");
        if(keys.isEmpty())return null;
        return keys.stream()
                .map(key->{
                    String value=redisTemplate.opsForValue().get(key);
                    if(value!=null){
                        try {
                            BookingReadModel bookingReadModel = objectMapper.readValue(value, BookingReadModel.class);
                            if (idempotencyKey.equals(bookingReadModel.getIdempotencyKey())) {
                                return bookingReadModel;
                            }
                        }
                        catch (JacksonException e) {
                            throw new RuntimeException("Failed to parse AirbnbReadModel read model from Redis",e);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}


















