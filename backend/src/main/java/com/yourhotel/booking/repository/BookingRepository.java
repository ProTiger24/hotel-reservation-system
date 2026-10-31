package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🚀 1. Used in BookingServiceImpl (Missing method fixed)
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(Booking.BookingStatus status);

    // 🚀 2. Used for User's My Bookings page
    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 🚀 3. Used for Check-in/Check-out daily view
    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user WHERE b.checkIn = :date OR b.checkOut = :date")
    List<Booking> findTodayBookings(@Param("date") LocalDate date);

    // 🚀 4. Used for Admin Bookings list
    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user ORDER BY b.createdAt DESC")
    List<Booking> findAllWithDetails();

    // 🚀 5. Used for Room availability check
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId AND b.status IN ('CONFIRMED', 'CHECKED_IN') AND b.checkIn <= :date AND b.checkOut > :date")
    long countActiveBookingsByRoom(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    // 🚀 6. Used for Conflict checking during new booking
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') AND (b.checkIn < :checkOut AND b.checkOut > :checkIn)")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);
}
