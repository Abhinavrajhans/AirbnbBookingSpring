package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.BookingStatus;
import com.example.AirbnbBookingSpring.models.readModels.BookingReadModel;
import com.example.AirbnbBookingSpring.repositories.reads.RedisReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService implements IIdempotencyService {

    private final RedisReadRepository redisReadRepository;

    @Override
    public boolean isIdempotencyKeyUsed(String IdempotencyKey) {
        return false;
    }

    @Override
    public Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey) {
        BookingReadModel bookingReadModel = redisReadRepository.findBookingByIdempotencyKey(idempotencyKey);
        if(bookingReadModel != null){
            Booking booking = Booking.builder()
                    .id(bookingReadModel.getId())
                    .airbnbId(bookingReadModel.getAirbnbId())
                    .userId(bookingReadModel.getUserId())
                    .totalPrice(bookingReadModel.getTotalPrice())
                    .bookingStatus(BookingStatus.valueOf(bookingReadModel.getBookingStatus()))
                    .idempotencyKey(bookingReadModel.getIdempotencyKey())
                    .checkInDate(bookingReadModel.getCheckInDate())
                    .checkOutDate(bookingReadModel.getCheckOutDate())
                    .build();
        }

    }




}
