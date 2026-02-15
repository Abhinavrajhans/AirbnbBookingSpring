package com.example.AirbnbBookingSpring.repositories.writes;

import com.example.AirbnbBookingSpring.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability,Long> {
    List<Availability> findByBookingId(Long BookingId);
    List<Availability> findByAirbnbId(String airbnbId);
}
