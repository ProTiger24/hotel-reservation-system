package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.VehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {
    List<VehicleBooking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
