package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.RoomRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.RoomResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.entity.RoomType;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.DuplicateResourceException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import com.yourhotel.booking.repository.RoomTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;

    public AdminRoomController(RoomRepository roomRepository,
                                RoomTypeRepository roomTypeRepository,
                                BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingRepository = bookingRepository;
    }

    // =========================
    // CREATE ROOM
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists: " + request.getRoomNumber());
        }

        RoomType roomType = findOrCreateRoomType(request);

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(roomType)
                .floor(request.getFloor())
                .status(Room.RoomStatus.AVAILABLE)
                .imageUrl(request.getImageUrl())
                .build();

        Room saved = roomRepository.save(room);
        return ResponseEntity.ok(ApiResponse.success("Room created", RoomResponse.fromEntity(saved)));
    }

    // =========================
    // UPDATE ROOM
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable Long id,
                                                                 @Valid @RequestBody RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getRoomNumber().equals(request.getRoomNumber())
            && roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists");
        }

        RoomType roomType = findOrCreateRoomType(request);

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(roomType);
        room.setFloor(request.getFloor());
        room.setImageUrl(request.getImageUrl());

        Room saved = roomRepository.save(room);
        return ResponseEntity.ok(ApiResponse.success("Room updated", RoomResponse.fromEntity(saved)));
    }

    // =========================
    // DELETE ROOM
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        roomRepository.delete(room);
        return ResponseEntity.ok(ApiResponse.success("Room deleted", null));
    }

    // =========================
    // UPDATE STATUS (Single)
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomResponse>> updateStatus(@PathVariable Long id,
                                                                    @RequestParam String status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        try {
            room.setStatus(Room.RoomStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }

        Room saved = roomRepository.save(room);
        return ResponseEntity.ok(ApiResponse.success("Status updated", RoomResponse.fromEntity(saved)));
    }

    // =========================
    // BULK DELETE
    // =========================
    @PostMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("No room IDs provided");
        }

        List<Room> rooms = roomRepository.findAllById(ids);
        roomRepository.deleteAll(rooms);

        Map<String, Object> result = new HashMap<>();
        result.put("deleted", rooms.size());

        return ResponseEntity.ok(ApiResponse.success("Rooms deleted", result));
    }

    // =========================
    // BULK STATUS UPDATE
    // =========================
    @PostMapping("/bulk-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkStatus(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> idsInt = (List<Integer>) body.get("ids");
        String status = (String) body.get("status");

        if (idsInt == null || idsInt.isEmpty() || status == null) {
            throw new BadRequestException("Missing room IDs or status");
        }

        List<Long> ids = idsInt.stream().map(Long::valueOf).collect(Collectors.toList());
        Room.RoomStatus newStatus;
        try {
            newStatus = Room.RoomStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }

        List<Room> rooms = roomRepository.findAllById(ids);
        rooms.forEach(r -> r.setStatus(newStatus));
        roomRepository.saveAll(rooms);

        Map<String, Object> result = new HashMap<>();
        result.put("updated", rooms.size());

        return ResponseEntity.ok(ApiResponse.success("Status updated for " + rooms.size() + " rooms", result));
    }

    // =========================
    // GET ALL ROOMS
    // =========================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomRepository.findAllWithRoomType().stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    // =========================
    // ANALYTICS (Room types + Top performers + Occupancy)
    // =========================
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        List<Room> rooms = roomRepository.findAllWithRoomType();
        List<Booking> bookings = bookingRepository.findAllWithDetails();

        // 1. Room Type Distribution
        Map<String, Long> roomTypeCounts = new LinkedHashMap<>();
        for (Room r : rooms) {
            String type = r.getRoomType().getName();
            roomTypeCounts.put(type, roomTypeCounts.getOrDefault(type, 0L) + 1);
        }

        // 2. Top Performing Rooms (Revenue based)
        Map<Long, BigDecimal> roomRevenue = new HashMap<>();
        Map<Long, Integer> roomBookings = new HashMap<>();

        for (Booking b : bookings) {
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED
                || b.getStatus() == Booking.BookingStatus.COMPLETED) {
                Long roomId = b.getRoom().getId();
                roomRevenue.put(roomId, roomRevenue.getOrDefault(roomId, BigDecimal.ZERO).add(b.getTotalPrice()));
                roomBookings.put(roomId, roomBookings.getOrDefault(roomId, 0) + 1);
            }
        }

        List<Map<String, Object>> topRooms = new ArrayList<>();
        for (Room r : rooms) {
            BigDecimal rev = roomRevenue.getOrDefault(r.getId(), BigDecimal.ZERO);
            int cnt = roomBookings.getOrDefault(r.getId(), 0);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("roomNumber", r.getRoomNumber());
            item.put("roomType", r.getRoomType().getName());
            item.put("revenue", rev);
            item.put("bookings", cnt);
            topRooms.add(item);
        }
        topRooms.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        if (topRooms.size() > 5) topRooms = topRooms.subList(0, 5);

        // 3. Occupancy for Next 7 Days
        LocalDate today = LocalDate.now();
        List<String> dateLabels = new ArrayList<>();
        List<Map<String, Object>> occupancyGrid = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate d = today.plusDays(i);
            dateLabels.add(d.toString());
        }

        for (Room r : rooms) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("roomId", r.getId());
            row.put("roomNumber", r.getRoomNumber());
            row.put("roomType", r.getRoomType().getName());

            List<Boolean> dayStatus = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate d = today.plusDays(i);
                boolean isBooked = bookings.stream().anyMatch(b ->
                    b.getRoom().getId().equals(r.getId())
                    && b.getStatus() == Booking.BookingStatus.CONFIRMED
                    && !d.isBefore(b.getCheckIn())
                    && d.isBefore(b.getCheckOut())
                );
                dayStatus.add(isBooked);
            }
            row.put("days", dayStatus);
            occupancyGrid.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("roomTypeCounts", roomTypeCounts);
        result.put("topRooms", topRooms);
        result.put("dateLabels", dateLabels);
        result.put("occupancyGrid", occupancyGrid);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private RoomType findOrCreateRoomType(RoomRequest request) {
        return roomTypeRepository.findAll().stream()
                .filter(rt -> rt.getName().equalsIgnoreCase(request.getRoomType()))
                .findFirst()
                .map(existing -> {
                    existing.setBasePrice(request.getPrice());
                    existing.setCapacity(request.getCapacity());
                    if (request.getDescription() != null && !request.getDescription().isEmpty()) {
                        existing.setDescription(request.getDescription());
                    }
                    return roomTypeRepository.save(existing);
                })
                .orElseGet(() -> roomTypeRepository.save(
                        RoomType.builder()
                                .name(request.getRoomType())
                                .description(request.getDescription() != null ? request.getDescription() : "")
                                .basePrice(request.getPrice())
                                .capacity(request.getCapacity())
                                .build()
                ));
    }
}
