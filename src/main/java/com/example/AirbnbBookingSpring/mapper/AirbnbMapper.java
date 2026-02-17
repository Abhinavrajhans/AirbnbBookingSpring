package com.example.AirbnbBookingSpring.mapper;

import com.example.AirbnbBookingSpring.dtos.AirbnbDTO;
import com.example.AirbnbBookingSpring.dtos.CreateAirbnbRequest;
import com.example.AirbnbBookingSpring.models.Airbnb;

public class AirbnbMapper {
    public static Airbnb toEntity(CreateAirbnbRequest request) {
        return Airbnb.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pricePerNight(request.getPricePerNight())
                .location(request.getLocation())
                .build();
    }

    public static AirbnbDTO toDTO(Airbnb airbnb) {
        return AirbnbDTO.builder()
                .id(airbnb.getId())
                .name(airbnb.getName())
                .description(airbnb.getDescription())
                .pricePerNight(airbnb.getPricePerNight())
                .location(airbnb.getLocation())
                .build();
    }
}

