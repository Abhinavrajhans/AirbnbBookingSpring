package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.dtos.CreateBookingRequest;
import com.example.AirbnbBookingSpring.dtos.UpdateBookingRequest;
import com.example.AirbnbBookingSpring.models.Booking;

public interface IBookingService {

    public Booking createBooking(CreateBookingRequest createBookingRequest);
    public Booking updateBooking(UpdateBookingRequest updateBookingRequest);
}
