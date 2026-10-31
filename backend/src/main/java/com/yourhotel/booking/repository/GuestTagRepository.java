package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.GuestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestTagRepository extends JpaRepository<GuestTag, Long> {
    List<GuestTag> findByGuestId(Long guestId);
    Optional<GuestTag> findByGuestIdAndTag(Long guestId, String tag);
    void deleteByGuestIdAndTag(Long guestId, String tag);
}
