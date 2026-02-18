package com.example.AirbnbBookingSpring.mapper;

import com.example.AirbnbBookingSpring.models.Booking;
import com.example.AirbnbBookingSpring.models.readModels.BookingReadModel;

// FIX: BookingMapper was completely empty — added conversion helpers
public class BookingMapper {

    public static BookingReadModel toReadModel(Booking booking) {
        return BookingReadModel.builder()
                .id(booking.getId())
                .airbnbId(booking.getAirbnb() != null ? booking.getAirbnb().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .totalPrice(booking.getTotalPrice())
                .bookingStatus(booking.getStatus().name())
                .idempotencyKey(booking.getIdempotencyKey())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .build();
    }
}