package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.dtos.CreateBookingRequest;
import com.example.AirbnbBookingSpring.dtos.UpdateBookingRequest;
import com.example.AirbnbBookingSpring.models.*;
import com.example.AirbnbBookingSpring.repositories.reads.RedisWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.AirbnbWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.BookingWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.UserWriteRepository;
import com.example.AirbnbBookingSpring.services.concurrency.ConcurrencyControlStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements IBookingService {

    private final BookingWriteRepository bookingWriteRepository;
    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final AirbnbWriteRepository airbnbWriteRepository;
    private final ConcurrencyControlStrategy concurrencyControlStrategy;
    private final UserWriteRepository userWriteRepository;
    private final RedisWriteRepository redisWriteRepository;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingRequest createBookingRequest) {
        Airbnb airbnb=airbnbWriteRepository.findById(createBookingRequest.getAirbnbId())
                .orElseThrow(()->new RuntimeException("Airbnb not found"));

        User user = userWriteRepository.findById(createBookingRequest.getUserId())
                .orElseThrow(()->new RuntimeException("User not found"));

        if(createBookingRequest.getCheckInDate().isAfter(createBookingRequest.getCheckOutDate())){
            throw new RuntimeException("Check-in date must be before Check-out date");
        }

        if(createBookingRequest.getCheckOutDate().isBefore(LocalDate.now())){
            throw new RuntimeException("Check-in Date must be today or in future");
        }

        List<Availability> availabilities = concurrencyControlStrategy.lockAndCheckAvailability(
                airbnb.getId(),
                createBookingRequest.getCheckInDate(),
                createBookingRequest.getCheckOutDate(),
                createBookingRequest.getUserId()
        );

        long nights= ChronoUnit.DAYS.between(createBookingRequest.getCheckInDate(),createBookingRequest.getCheckOutDate());
        double pricePerNight= airbnb.getPricePerNight();
        double totalPrice = nights*pricePerNight;

        String idempotencyKey = UUID.randomUUID().toString();
        log.info("Creating booking for Airbnb {} with check-in date {} and check-out date {} and total price {} and idempotency key {}",
                airbnb.getId(), createBookingRequest.getCheckInDate(), createBookingRequest.getCheckOutDate(), totalPrice, idempotencyKey);



        Booking booking = Booking.builder()
                .airbnb(airbnb)
                .user(user)
                .checkInDate(createBookingRequest.getCheckInDate())
                .checkOutDate(createBookingRequest.getCheckOutDate())
                .idempotencyKey(idempotencyKey)
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingWriteRepository.save(booking);
        redisWriteRepository.writeBookingReadModel(booking);
        return booking;
    }

    @Override
    public Booking updateBooking(UpdateBookingRequest updateBookingRequest) {
        return null;
    }
}
