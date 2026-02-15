package com.example.AirbnbBookingSpring.services.concurrency;

import com.example.AirbnbBookingSpring.models.Availability;
import java.util.List;
import java.time.LocalDate;

public interface ConcurrencyControlStrategy {

    public void releaseLock(long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);
    public List<Availability> lockAndCheckAvailability(long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);
}
