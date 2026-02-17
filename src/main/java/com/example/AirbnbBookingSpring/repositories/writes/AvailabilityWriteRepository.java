package com.example.AirbnbBookingSpring.repositories.writes;

import com.example.AirbnbBookingSpring.models.Availability;
import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByBooking(Booking booking); // UPDATED
    List<Availability> findByAirbnb(Airbnb airbnb); // UPDATED
    List<Availability> findByAirbnbAndDate(Airbnb airbnb, String date);
}