package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.BookingStatus;
import com.example.AirbnbBookingSpring.models.readModels.BookingReadModel;
import com.example.AirbnbBookingSpring.repositories.reads.RedisReadRepository;
import com.example.AirbnbBookingSpring.repositories.writes.BookingWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService implements IIdempotencyService {

    private final RedisReadRepository redisReadRepository;
    private final BookingWriteRepository bookingWriteRepository; // fallback if Redis misses

    @Override
    public boolean isIdempotencyKeyUsed(String idempotencyKey) {
        return this.findBookingByIdempotencyKey(idempotencyKey).isPresent();
    }

    // FIX: was building a Booking object but never returning it
    @Override
    public Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey) {
        // Try Redis read model first
        BookingReadModel bookingReadModel = redisReadRepository.findBookingByIdempotencyKey(idempotencyKey);
        if (bookingReadModel != null) {
            // NOTE: This is a lightweight Booking with only scalar fields populated.
            // Do NOT use this for operations that rely on the full airbnb/user relationships.
            // For those, call bookingWriteRepository.findByIdempotencyKey() instead.
            Booking booking = Booking.builder()
                    .idempotencyKey(bookingReadModel.getIdempotencyKey())
                    .totalPrice(bookingReadModel.getTotalPrice())
                    .status(BookingStatus.valueOf(bookingReadModel.getBookingStatus()))
                    .checkInDate(bookingReadModel.getCheckInDate())
                    .checkOutDate(bookingReadModel.getCheckOutDate())
                    .build();
            booking.setId(bookingReadModel.getId());
            return Optional.of(booking);  // FIX: was missing this return
        }
        // Fallback to DB for full entity
        return bookingWriteRepository.findByIdempotencyKey(idempotencyKey);
    }
}