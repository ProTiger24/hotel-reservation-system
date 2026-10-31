package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.entity.HousekeepingStaff;
import com.yourhotel.booking.entity.HousekeepingTask;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.repository.HousekeepingStaffRepository;
import com.yourhotel.booking.repository.HousekeepingTaskRepository;
import com.yourhotel.booking.repository.MaintenanceRequestRepository;
import com.yourhotel.booking.entity.MaintenanceRequest;
import com.yourhotel.booking.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/housekeeping")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHousekeepingController {

    private final HousekeepingStaffRepository staffRepository;
    private final HousekeepingTaskRepository taskRepository;
    private final RoomRepository roomRepository;
    private final MaintenanceRequestRepository maintenanceRepository;

    public AdminHousekeepingController(HousekeepingStaffRepository staffRepository,
                                       HousekeepingTaskRepository taskRepository,
                                       RoomRepository roomRepository,
                                       MaintenanceRequestRepository maintenanceRepository) {
        this.staffRepository = staffRepository;
        this.taskRepository = taskRepository;
        this.roomRepository = roomRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    // 1. Get all staff (To populate dropdown)
    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<HousekeepingStaff>>> getStaff() {
        List<HousekeepingStaff> staff = staffRepository.findAll();
        
        // 🚀 Dummy Data: If no staff exists, create some for testing
        if (staff.isEmpty()) {
            staffRepository.saveAll(List.of(
                HousekeepingStaff.builder().name("Rahim (Cleaner)").phone("01711111111").shift("MORNING").status(HousekeepingStaff.StaffStatus.AVAILABLE).build(),
                HousekeepingStaff.builder().name("Karim (Cleaner)").phone("01822222222").shift("EVENING").status(HousekeepingStaff.StaffStatus.AVAILABLE).build()
            ));
            staff = staffRepository.findAll();
        }
        return ResponseEntity.ok(ApiResponse.success(staff));
    }

    // 2. Create a new Task
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<HousekeepingTask>> createTask(@RequestBody Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Long staffId = payload.get("staffId") != null ? Long.valueOf(payload.get("staffId").toString()) : null;
        String typeStr = (String) payload.get("type");
        String notes = (String) payload.get("notes");

        Room room = roomRepository.findById(roomId).orElseThrow();
        HousekeepingStaff staff = staffId != null ? staffRepository.findById(staffId).orElse(null) : null;

        HousekeepingTask task = HousekeepingTask.builder()
                .room(room)
                .assignedStaff(staff)
                .type(HousekeepingTask.TaskType.valueOf(typeStr))
                .status(HousekeepingTask.TaskStatus.PENDING)
                .notes(notes)
                .build();

        // Automatically update room status to CLEANING if task is created
        room.setStatus(Room.RoomStatus.CLEANING);
        roomRepository.save(room);

        return ResponseEntity.ok(ApiResponse.success("Task created", taskRepository.save(task)));
    }

    @PostMapping("/maintenance")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> createMaintenance(@RequestBody Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        String issue = (String) payload.get("issueDescription");
        String priorityStr = (String) payload.get("priority");

        Room room = roomRepository.findById(roomId).orElseThrow();
        
        MaintenanceRequest request = new MaintenanceRequest();
        request.setRoom(room);
        request.setIssueDescription(issue);
        request.setPriority(MaintenanceRequest.Priority.valueOf(priorityStr));
        request.setStatus(MaintenanceRequest.RequestStatus.OPEN);

        return ResponseEntity.ok(ApiResponse.success("Maintenance request logged", maintenanceRepository.save(request)));
    }
}
