package com.example.AirbnbBookingSpring.repositories.writes;

import com.example.AirbnbBookingSpring.models.Availability;
import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByBooking(Booking booking); // UPDATED
    List<Availability> findByAirbnb(Airbnb airbnb); // UPDATED
    List<Availability> findByAirbnbAndDate(Airbnb airbnb, LocalDate date);

    //select * from availability where airbnb_id=airbnbId and date BETWEEN startDate and EndDate
    List<Availability> findByAirbnbIdAndDateBetween(Long airbnbId, LocalDate startDate, LocalDate endDate);

    // select count(*) from availability where airbnb_id=airbnbId and date BETWEEN startDate and EndDate and bookingId is not null
    Long countByAirbnbIdAndDateBetweenAndBookingIsNotNull(Long airbnbId, LocalDate startDate, LocalDate endDate);

    // UPDATE availability set booking_id = bookingId where airbnb_id = airbnbId and date between startDate and endDate.
    @Modifying
    @Query("UPDATE Availability a SET a.bookingId = :bookingId where a.airbnbId = :airbnbId and a.date BETWEEN :startDate and :endDate")
    void updateBookingIdByAirbnbIdAndDateBetween(Long bookingId,Long airbnbId, LocalDate startDate, LocalDate endDate);

}