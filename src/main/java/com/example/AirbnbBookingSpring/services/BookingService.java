package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.dtos.CreateBookingRequest;
import com.example.AirbnbBookingSpring.dtos.UpdateBookingRequest;
import com.example.AirbnbBookingSpring.models.*;
import com.example.AirbnbBookingSpring.repositories.reads.RedisWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.AirbnbWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.BookingWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.UserWriteRepository;
import com.example.AirbnbBookingSpring.saga.SagaEventPublisher;
import com.example.AirbnbBookingSpring.services.concurrency.ConcurrencyControlStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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
    private final IIdempotencyService idempotencyService;
    private final SagaEventPublisher  sagaEventPublisher;

    // FIX: Added idempotency check + totalPrice on booking + lock release in finally
    @Override
    @Transactional
    public Booking createBooking(CreateBookingRequest createBookingRequest) {
        Airbnb airbnb = airbnbWriteRepository.findById(createBookingRequest.getAirbnbId())
                .orElseThrow(() -> new RuntimeException("Airbnb not found"));

        User user = userWriteRepository.findById(createBookingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (createBookingRequest.getCheckInDate().isAfter(createBookingRequest.getCheckOutDate())) {
            throw new RuntimeException("Check-in date must be before Check-out date");
        }

        if (createBookingRequest.getCheckOutDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-out date must be today or in the future");
        }

        LocalDate checkIn = createBookingRequest.getCheckInDate();
        LocalDate checkOut = createBookingRequest.getCheckOutDate();

        List<Availability> availabilities = concurrencyControlStrategy.lockAndCheckAvailability(
                airbnb.getId(), checkIn, checkOut, createBookingRequest.getUserId()
        );

        try {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalPrice = nights * airbnb.getPricePerNight();

            String idempotencyKey = UUID.randomUUID().toString();
            log.info("Creating booking for Airbnb {} | dates: {} → {} | total: {} | key: {}",
                    airbnb.getId(), checkIn, checkOut, totalPrice, idempotencyKey);

            // Mark availability slots as booked
            availabilities.forEach(slot -> {
                slot.setIsAvailable(false);
            });
            availabilityWriteRepository.saveAll(availabilities);

            // FIX: totalPrice now set on the Booking entity
            Booking booking = Booking.builder()
                    .airbnb(airbnb)
                    .user(user)
                    .checkInDate(checkIn)
                    .checkOutDate(checkOut)
                    .totalPrice(totalPrice)
                    .idempotencyKey(idempotencyKey)
                    .status(BookingStatus.PENDING)
                    .build();

            booking = bookingWriteRepository.save(booking);
            redisWriteRepository.writeBookingReadModel(booking);
            return booking;

        } finally {
            // FIX: Always release the Redis lock (success or failure)
            concurrencyControlStrategy.releaseLock(airbnb.getId(), checkIn, checkOut);
        }
    }

    // FIX: Implemented updateBooking with idempotency check
    @Override
    @Transactional
    public Booking updateBooking(UpdateBookingRequest updateBookingRequest) {
        log.info("Updating Booking for idempotency key {}",updateBookingRequest.getIdempotencyKey());
        Booking booking = idempotencyService.findBookingByIdempotencyKey(
                updateBookingRequest.getIdempotencyKey()
                )
                .orElseThrow(()-> new RuntimeException("Booking not found"));


        log.info("booking found for idempotency key {}",updateBookingRequest.getIdempotencyKey());
        log.info("booking status {}:",booking.getStatus());
        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking status is not PENDING");
        }

        Map<String, Object> payload = Map.of(
                "bookingId",   booking.getId(),
                "airbnbId",    booking.getAirbnb().getId(),   // safe: fully hydrated from DB
                "checkInDate", booking.getCheckInDate().toString(),
                "checkOutDate", booking.getCheckOutDate().toString()
        );

        if(updateBookingRequest.getBookingStatus()==BookingStatus.CONFIRMED){ // TODO : This also violates a solid principle please resolve it.
            sagaEventPublisher.publishEvent("BOOKING_CONFIRM_REQUESTED","CONFIRM_BOOKING",payload);
        }
        else if(updateBookingRequest.getBookingStatus()==BookingStatus.CANCELLED) {
            sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED","CANCEL_BOOKING",payload);
        }

        return booking;


    }
}