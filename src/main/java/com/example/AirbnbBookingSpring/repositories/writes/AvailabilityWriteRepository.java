package com.example.AirbnbBookingSpring.repositories.writes;

import com.example.AirbnbBookingSpring.models.Availability;
import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBooking(Booking booking);
    List<Availability> findByAirbnb(Airbnb airbnb);
    List<Availability> findByAirbnbAndDate(Airbnb airbnb, LocalDate date);

    List<Availability> findByAirbnbIdAndDateBetween(Long airbnbId, LocalDate startDate, LocalDate endDate);

    Long countByAirbnbIdAndDateBetweenAndBookingIsNotNull(Long airbnbId, LocalDate startDate, LocalDate endDate);

    /**
     * FIX: The original query used `a.airbnbId` and `a.bookingId` which are NOT valid JPQL
     * field names — these are JPA relationships, not plain columns.
     * Correct JPQL references the *relationship object*: a.airbnb.id and a.booking.id.
     *
     * FIX: Added @Transactional — @Modifying queries must run inside a transaction.
     *
     * FIX: Added @Param annotations so Spring can bind named params correctly.
     *
     * NOTE: Setting booking to null (for cancellations) is handled as a native query
     * because JPQL cannot set a relationship field to null via UPDATE in all JPA providers.
     * Use the native variant below if your provider complains.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Availability a SET a.booking.id = :bookingId " +
            "WHERE a.airbnb.id = :airbnbId AND a.date BETWEEN :startDate AND :endDate")
    void updateBookingIdByAirbnbIdAndDateBetween(
            @Param("bookingId") Long bookingId,
            @Param("airbnbId")  Long airbnbId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate);

    /**
     * Use this overload when clearing (cancelling) a booking — sets booking_id to NULL.
     * JPQL cannot set a relationship to null, so a native query is required.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE availabilities SET booking_id = NULL " +
            "WHERE airbnb_id = :airbnbId AND date BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    void clearBookingByAirbnbIdAndDateBetween(
            @Param("airbnbId")  Long airbnbId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate);
}