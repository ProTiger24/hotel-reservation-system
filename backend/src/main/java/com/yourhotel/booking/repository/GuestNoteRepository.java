package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.GuestNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestNoteRepository extends JpaRepository<GuestNote, Long> {
    List<GuestNote> findByGuestIdOrderByCreatedAtDesc(Long guestId);
}
