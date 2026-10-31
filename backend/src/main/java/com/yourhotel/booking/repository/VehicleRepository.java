package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);
    List<Vehicle> findByVehicleType(String vehicleType);
    boolean existsByVehicleNumber(String vehicleNumber);
}
