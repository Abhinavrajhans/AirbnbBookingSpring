package com.example.AirbnbBookingSpring.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability extends BaseModel {

    @Column(nullable = false)
    private String airbnbId;
    @Column(nullable = false)
    private String date;
    private Long bookingId; // null if available
}
