package com.example.AirbnbBookingSpring.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirbnbDTO {
    private Long id;
    private String name;
    private String description;
    private Long pricePerNight;
    private String location;
}