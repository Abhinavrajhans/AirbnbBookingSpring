package com.example.AirbnbBookingSpring.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {

    @NotNull(message="Airbnb Id is Required")
    private Long airbnbId;

    @NotNull(message="CheckInDate is Required")
    private LocalDate checkInDate;

    @NotNull(message="CheckOutDate is Required")
    private LocalDate checkOutDate;

    @NotNull(message="User Id is Required")
    private Long userId;

}
