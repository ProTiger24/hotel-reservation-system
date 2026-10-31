package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public AdminStatsController(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    // 🚀 1. Get Stats for Charts and Cards
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        List<Booking> bookings = bookingRepository.findAllWithDetails();
        List<Room> rooms = roomRepository.findAllWithRoomType();

        // Basic Stats
        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED)
                .map(Booking::getTotalPrice).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalBookings = bookings.size();
        long occupiedRooms = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.OCCUPIED).count();
        double occupancyRate = rooms.isEmpty() ? 0 : ((double) occupiedRooms / rooms.size()) * 100;

        double adr = 0;
        long roomNights = 0;
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED) {
                if (b.getCheckIn() != null && b.getCheckOut() != null) {
                    long nights = ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());
                    if (nights > 0) roomNights += nights;
                }
            }
        }
        if (roomNights > 0) adr = totalRevenue.doubleValue() / roomNights;

        // Monthly Revenue
        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            monthlyRevenue.put(now.minusMonths(i).getMonth().name().substring(0, 3), BigDecimal.ZERO);
        }

        for (Booking b : bookings) {
            if ((b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED) && b.getCheckIn() != null) {
                if (!b.getCheckIn().isBefore(now.minusMonths(5).withDayOfMonth(1))) {
                    String monthKey = b.getCheckIn().getMonth().name().substring(0, 3);
                    if (monthlyRevenue.containsKey(monthKey)) {
                        monthlyRevenue.put(monthKey, monthlyRevenue.get(monthKey).add(b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO));
                    }
                }
            }
        }

        // Room Distribution
        Map<String, Long> roomTypeStats = new HashMap<>();
        for (Room r : rooms) {
            String type = (r.getRoomType() != null && r.getRoomType().getName() != null) ? r.getRoomType().getName() : "Unknown";
            roomTypeStats.put(type, roomTypeStats.getOrDefault(type, 0L) + 1);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalBookings", totalBookings);
        stats.put("occupancyRate", Math.round(occupancyRate));
        stats.put("averageDailyRate", Math.round(adr));
        stats.put("revenueLabels", new ArrayList<>(monthlyRevenue.keySet()));
        stats.put("revenueData", new ArrayList<>(monthlyRevenue.values()));
        stats.put("roomTypeLabels", new ArrayList<>(roomTypeStats.keySet()));
        stats.put("roomTypeData", new ArrayList<>(roomTypeStats.values()));

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // 🚀 2. Get Recent Bookings for Table (Safe Map Version)
    @GetMapping("/recent-bookings")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecentBookings() {
        List<Map<String, Object>> list = bookingRepository.findAllWithDetails().stream()
                .limit(5)
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getId());
                    map.put("userName", b.getUser() != null ? b.getUser().getName() : "Guest");
                    map.put("roomNumber", b.getRoom() != null ? b.getRoom().getRoomNumber() : "—");
                    map.put("totalPrice", b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO);
                    map.put("status", b.getStatus() != null ? b.getStatus().name() : "UNKNOWN");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
