package com.example.AirbnbBookingSpring.handlers;

import com.example.AirbnbBookingSpring.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBookingSpring.saga.SagaEvent;
import com.example.AirbnbBookingSpring.saga.SagaEventProcessor;
import com.example.AirbnbBookingSpring.saga.SagaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvailabilityEventHandler {

    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;
    private final SagaEventProcessor sagaEventProcessor;

    public void handleBookingConfirmed(SagaEvent sagaEvent){
        try{
            Map<String,Object> payload = sagaEvent.getPayload();
            Long bookingId=Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId =Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate=LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate=LocalDate.parse(payload.get("checkOutDate").toString());

            Long count=availabilityWriteRepository.countByAirbnbIdAndDateBetweenAndBookingIsNotNull(bookingId,checkInDate,checkOutDate);
            if(count>0){
                sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED","CANCEL_BOOKING",payload);
                throw new RuntimeException("Airbnb is not available for the given dates. Please try again with different dates");
            }
            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(bookingId,airbnbId,checkInDate,checkOutDate);
            sagaEventPublisher.publishEvent("BOOKING_CONFIRMED","CONFIRM_BOOKING",payload);
        }
        catch (Exception e){
            Map<String,Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED","COMPENSATED_BOOKING",payload);
            throw new RuntimeException("failed to confirm booking",e);
        }
    }

    public void handleBookingCancelled(SagaEvent sagaEvent){
        try{
            Map<String,Object> payload = sagaEvent.getPayload();
            Long bookingId=Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId =Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate=LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate=LocalDate.parse(payload.get("checkOutDate").toString());
            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(null,airbnbId,checkInDate,checkOutDate);
            sagaEventPublisher.publishEvent("BOOKING_CANCELLED","CANCEL_BOOKING",payload);
        }
        catch (Exception e){
            Map<String,Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED","COMPENSATE_BOOKING",payload);
            throw new RuntimeException("failed to cancel booking",e);
        }
    }

}
