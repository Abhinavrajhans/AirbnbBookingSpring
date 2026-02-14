package com.example.AirbnbBookingSpring.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseModel{

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String airbnbId;

    @Column(nullable = false)
    private String totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status=BookingStatus.PENDING;

    @Column(unique = true)
    private String idempotencyKey;
}
