package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import com.yourhotel.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        List<Booking> bookings = bookingRepository.findAllWithDetails();
        List<Room> rooms = roomRepository.findAllWithRoomType();
        LocalDate today = LocalDate.now();

        // 1. Basic Stats
        long totalBookings = bookings.size();
        long pendingBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long confirmedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();
        
        long totalRooms = rooms.size();
        long availableRooms = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE).count();
        long occupiedRooms = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.OCCUPIED).count();
        long totalUsers = userRepository.count();

        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double occupancyRate = totalRooms == 0 ? 0 : ((double) occupiedRooms / totalRooms) * 100;

        // 2. Chart Data: Revenue Map (YYYY-MM -> Amount)
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = today.minusMonths(i);
            String key = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            revenueMap.put(key, BigDecimal.ZERO);
        }

        for (Booking b : bookings) {
            if ((b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED) && b.getCheckIn() != null) {
                String key = b.getCheckIn().getYear() + "-" + String.format("%02d", b.getCheckIn().getMonthValue());
                if (revenueMap.containsKey(key)) {
                    revenueMap.put(key, revenueMap.get(key).add(b.getTotalPrice()));
                }
            }
        }

        // 3. Chart Data: Room Status Map
        Map<String, Long> roomStatusMap = new HashMap<>();
        roomStatusMap.put("AVAILABLE", availableRooms);
        roomStatusMap.put("BOOKED", occupiedRooms); // Frontend expects BOOKED for OCCUPIED
        roomStatusMap.put("MAINTENANCE", rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE).count());

        // 4. Chart Data: Booking Status Map
        Map<String, Long> bookingStatusMap = new HashMap<>();
        bookingStatusMap.put("PENDING", pendingBookings);
        bookingStatusMap.put("CONFIRMED", confirmedBookings);
        bookingStatusMap.put("CANCELLED", bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count());
        bookingStatusMap.put("COMPLETED", bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count());

        // Prepare Response
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("totalRooms", totalRooms);
        stats.put("availableRooms", availableRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("totalUsers", totalUsers);
        stats.put("totalRevenue", totalRevenue);
        stats.put("occupancyRate", Math.round(occupancyRate));
        
        // Add formatted data for charts
        stats.put("revenue", revenueMap);
        stats.put("roomStatus", roomStatusMap);
        stats.put("bookingStatus", bookingStatusMap);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/bookings/recent")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecentBookings() {
        List<Map<String, Object>> recent = bookingRepository.findAllWithDetails().stream()
                .limit(5)
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getId());
                    map.put("userName", b.getUser() != null ? b.getUser().getName() : "Guest");
                    map.put("userEmail", b.getUser() != null ? b.getUser().getEmail() : "N/A");
                    map.put("roomNumber", b.getRoom() != null ? b.getRoom().getRoomNumber() : "N/A");
                    map.put("roomType", b.getRoom() != null && b.getRoom().getRoomType() != null ? b.getRoom().getRoomType().getName() : "N/A");
                    map.put("totalPrice", b.getTotalPrice());
                    map.put("status", b.getStatus().name());
                    map.put("checkIn", b.getCheckIn());
                    map.put("checkOut", b.getCheckOut());
                    map.put("createdAt", b.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(recent));
    }
}
