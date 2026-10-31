package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    List<HousekeepingTask> findByStatusIn(List<HousekeepingTask.TaskStatus> statuses);
    List<HousekeepingTask> findByRoomIdOrderByCreatedAtDesc(Long roomId);
}
