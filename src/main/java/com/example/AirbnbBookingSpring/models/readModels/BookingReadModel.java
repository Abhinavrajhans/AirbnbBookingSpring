package com.example.AirbnbBookingSpring.models.readModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReadModel {
    private Long id;
    private Long airbnbId;
    private Long userId;
    private Long totalPrice;
    private String bookingStatus;
    private String idempotencyKey;
    private String checkInDate;
    private String checkOutDate;
}