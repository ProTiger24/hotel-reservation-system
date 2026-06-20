package com.hotel.repository;

import com.hotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGuestEmail(String email);

    List<Booking> findByStatus(String status);

    List<Booking> findByRoomId(Long roomId);
}