package com.example.AirbnbBookingSpring.repositories.reads;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.readModels.AirbnbReadModel;
import com.example.AirbnbBookingSpring.models.readModels.AvailabilityReadModel;
import com.example.AirbnbBookingSpring.models.readModels.BookingReadModel;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisWriteRepository {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String,String> redisTemplate;

    public void writeBookingReadModel(Booking booking){
        BookingReadModel bookingReadModel = BookingReadModel.builder()
                .id(booking.getId())
                .airbnbId(booking.getAirbnb().getId())
                .userId(booking.getUser().getId())
                .totalPrice(booking.getTotalPrice())
                .bookingStatus(booking.getStatus().name())
                .idempotencyKey(booking.getIdempotencyKey())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .build();

        saveBookingReadModel(bookingReadModel);
    }

    public void writeAirbnbReadModel(AirbnbReadModel airbnbReadModel) {
        String key = RedisReadRepository.AIRBNB_KEY_PREFIX + airbnbReadModel.getId();
        try {
            String value = objectMapper.writeValueAsString(airbnbReadModel);
            redisTemplate.opsForValue().set(key, value);
        } catch (JacksonException e) {  // FIX: was unhandled checked exception
            throw new RuntimeException("Failed to serialize AirbnbReadModel to Redis", e);
        }
    }

    public void writeAvailabilityReadModel(AvailabilityReadModel availabilityReadModel) {
        String key = RedisReadRepository.AVAILABLE_KEY_PREFIX + availabilityReadModel.getId();
        try {
            String value = objectMapper.writeValueAsString(availabilityReadModel);
            redisTemplate.opsForValue().set(key, value);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize AvailabilityReadModel to Redis", e);
        }
    }

    // FIX: was missing try/catch for JsonProcessingException (checked exception)
    private void saveBookingReadModel(BookingReadModel bookingReadModel) {
        String key = RedisReadRepository.BOOKING_KEY_PREFIX + bookingReadModel.getId();
        try {
            String value = objectMapper.writeValueAsString(bookingReadModel);
            redisTemplate.opsForValue().set(key, value);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize BookingReadModel to Redis", e);
        }
    }
}