package com.example.AirbnbBookingSpring.handlers;

import com.example.AirbnbBookingSpring.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBookingSpring.saga.SagaEvent;
import com.example.AirbnbBookingSpring.saga.SagaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityEventHandler {

    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;

    public void handleBookingConfirmed(SagaEvent sagaEvent){
        try{
            Map<String,Object> payload = sagaEvent.getPayload();
            Long bookingId=Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId =Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate=LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate=LocalDate.parse(payload.get("checkOutDate").toString());

            Long count=availabilityWriteRepository.countByAirbnbIdAndDateBetweenAndBookingIsNotNull(airbnbId,checkInDate,checkOutDate);
            if(count>0){
                sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED","CANCEL_BOOKING",payload);
                throw new RuntimeException("Airbnb is not available for the given dates. Please try again with different dates");
            }
            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(bookingId,airbnbId,checkInDate,checkOutDate);

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
            availabilityWriteRepository.clearBookingByAirbnbIdAndDateBetween(airbnbId, checkInDate, checkOutDate);

        }
        catch (Exception e){
            log.error("handleBookingCancelled failed for sagaId={}: {}", sagaEvent.getSagaId(), e.getMessage());
            Map<String,Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED","COMPENSATE_BOOKING",payload);
            throw new RuntimeException("failed to cancel booking",e);
        }
    }

}
