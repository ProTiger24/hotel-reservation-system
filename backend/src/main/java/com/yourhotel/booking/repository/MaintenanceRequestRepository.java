package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStatusIn(List<MaintenanceRequest.RequestStatus> statuses);
}
