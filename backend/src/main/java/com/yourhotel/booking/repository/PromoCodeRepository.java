package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeAndIsActiveTrue(String code);
    boolean existsByCode(String code);
}
