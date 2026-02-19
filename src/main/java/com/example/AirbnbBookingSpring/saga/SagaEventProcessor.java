package com.example.AirbnbBookingSpring.saga;

import com.example.AirbnbBookingSpring.handlers.AvailabilityEventHandler;
import com.example.AirbnbBookingSpring.handlers.BookingEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaEventProcessor {

    private final BookingEventHandler bookingEventHandler;
    private final AvailabilityEventHandler availabilityEventHandler;

    public void processEvent(SagaEvent sagaEvent){
        switch (sagaEvent.getEventType()){
            case "BOOKING_CREATED":
                //NO ACTION
                break;
            case "BOOKING_CONFIRM_REQUESTED":
                bookingEventHandler.handleBookingConfirmRequested(sagaEvent);
                break;
            case "BOOKING_CONFIRMED":
                availabilityEventHandler.handleBookingConfirmed(sagaEvent);
                log.info("Booking comfired for booking id: {}",sagaEvent.getPayload().get("bookingId"));
                break;
            case "BOOKING_CANCEL_REQUESTED":
                bookingEventHandler.handleBookingCancelRequested(sagaEvent);
                break;
            case "BOOKING_CANCELLED":
                availabilityEventHandler.handleBookingCancelled(sagaEvent);
                log.info("Booking Cancelled for booking id: {}",sagaEvent.getPayload().get("bookingId"));
                break;
            case "BOOKING_COMPENSATED":
                log.info("Booking compensated  for booking id: {}",sagaEvent.getPayload().get("bookingId"));
                break;
            default:

                break;


        }
    }
}
