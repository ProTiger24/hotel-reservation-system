package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
    List<TravelPackage> findByIsActiveTrue();
    List<TravelPackage> findByDestinationId(Long destinationId);
}
