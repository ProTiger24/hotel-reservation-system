package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);
    List<ActivityLog> findTop50ByOrderByCreatedAtDesc();
}
