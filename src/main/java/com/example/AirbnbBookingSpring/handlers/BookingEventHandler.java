package com.example.AirbnbBookingSpring.handlers;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.BookingStatus;
import com.example.AirbnbBookingSpring.repositories.reads.RedisWriteRepository;
import com.example.AirbnbBookingSpring.repositories.writes.BookingWriteRepository;
import com.example.AirbnbBookingSpring.saga.SagaEvent;
import com.example.AirbnbBookingSpring.saga.SagaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingEventHandler {

    private final BookingWriteRepository bookingWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;
    private final RedisWriteRepository redisWriteRepository;

    @Transactional
    public void handleBookingConfirmRequested(SagaEvent sagaEvent){
        try {
            Map<String, Object> payload = sagaEvent.getPayload();
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());
            Booking booking = bookingWriteRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking Not Found"));
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingWriteRepository.save(booking);
            //we can save to redis using CDC also
            redisWriteRepository.writeBookingReadModel(booking);
            sagaEventPublisher.publishEvent(
                    "BOOKING_CONFIRMED", "CONFIRM_BOOKING",
                    Map.of("bookingId", bookingId,
                            "airbnbId", airbnbId,
                            "checkInDate", checkInDate.toString(),
                            "checkOutDate", checkOutDate.toString()
                    ));
        }
        catch (Exception e){
            Map<String, Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED","COMPENSATE_BOOKING",payload);
            throw new RuntimeException("Failed to comfirm booking",e);
        }
    }

    @Transactional
    public void handleBookingCancelRequested(SagaEvent sagaEvent){
        try {
            Map<String, Object> payload = sagaEvent.getPayload();
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());
            Booking booking = bookingWriteRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking Not Found"));
            booking.setStatus(BookingStatus.CANCELLED);
            bookingWriteRepository.save(booking);
            //write it to redis
            //we can save to redis using CDC also
            redisWriteRepository.writeBookingReadModel(booking);

            sagaEventPublisher.publishEvent(
                    "BOOKING_CANCELLED", "CANCEL_BOOKING",
                    Map.of("bookingId", bookingId,
                            "airbnbId", airbnbId,
                            "checkInDate", checkInDate.toString(),
                            "checkOutDate", checkOutDate.toString()
                    ));
        }
        catch (Exception e){
            Map<String, Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED","COMPENSATE_BOOKING",payload);
            //in the compensation logic add the current booking status also and then u have to compensate just update back to
            //the current booking status
            throw new RuntimeException("Failed to comfirm booking",e);
        }
    }

    public void handleBookingCompensation(SagaEvent sagaEvent){

    }
}
