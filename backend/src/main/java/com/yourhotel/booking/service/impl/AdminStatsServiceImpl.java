package com.yourhotel.booking.service.impl;

import com.yourhotel.booking.dto.response.AdminStatsResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import com.yourhotel.booking.repository.UserRepository;
import com.yourhotel.booking.service.AdminStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AdminStatsServiceImpl(BookingRepository bookingRepository,
                                 RoomRepository roomRepository,
                                 UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStats() {
        List<Booking> allBookings = bookingRepository.findAllWithDetails();
        List<Room> allRooms = roomRepository.findAll();

        long totalRooms = allRooms.size();
        long availableRooms = allRooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE).count();
        long occupiedRooms = allRooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.BOOKED).count();
        long maintenanceRooms = allRooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE).count();

        long totalBookings = allBookings.size();
        long pendingBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long confirmedBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();
        long cancelledBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
        long completedBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();

        long totalUsers = userRepository.count();

        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED
                          || b.getStatus() == Booking.BookingStatus.COMPLETED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth currentMonth = YearMonth.now();
        BigDecimal monthlyRevenue = allBookings.stream()
                .filter(b -> (b.getStatus() == Booking.BookingStatus.CONFIRMED
                           || b.getStatus() == Booking.BookingStatus.COMPLETED))
                .filter(b -> YearMonth.from(b.getCreatedAt()).equals(currentMonth))
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate today = LocalDate.now();
        BigDecimal todayRevenue = allBookings.stream()
                .filter(b -> (b.getStatus() == Booking.BookingStatus.CONFIRMED
                           || b.getStatus() == Booking.BookingStatus.COMPLETED))
                .filter(b -> b.getCreatedAt().toLocalDate().equals(today))
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double occupancyRate = totalRooms > 0 ? ((double) occupiedRooms / totalRooms) * 100 : 0;
        double cancellationRate = totalBookings > 0 ? ((double) cancelledBookings / totalBookings) * 100 : 0;

        // ✅ NEW — Today's Activity
        long todayCheckIns = allBookings.stream()
                .filter(b -> b.getCheckIn().equals(today))
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        long todayCheckOuts = allBookings.stream()
                .filter(b -> b.getCheckOut().equals(today))
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        long pendingPayments = allBookings.stream()
                .filter(b -> "PENDING".equals(b.getPaymentStatus()))
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .count();

        // Charts
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        Map<String, Long> bookingsByMonth = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            String key = ym.format(formatter);
            revenueByMonth.put(key, BigDecimal.ZERO);
            bookingsByMonth.put(key, 0L);
        }

        for (Booking b : allBookings) {
            String key = YearMonth.from(b.getCreatedAt()).format(formatter);
            if (revenueByMonth.containsKey(key)) {
                if (b.getStatus() == Booking.BookingStatus.CONFIRMED
                 || b.getStatus() == Booking.BookingStatus.COMPLETED) {
                    revenueByMonth.put(key, revenueByMonth.get(key).add(b.getTotalPrice()));
                }
                bookingsByMonth.put(key, bookingsByMonth.get(key) + 1);
            }
        }

        Map<String, Long> bookingsByStatus = new LinkedHashMap<>();
        bookingsByStatus.put("PENDING", pendingBookings);
        bookingsByStatus.put("CONFIRMED", confirmedBookings);
        bookingsByStatus.put("CANCELLED", cancelledBookings);
        bookingsByStatus.put("COMPLETED", completedBookings);

        Map<String, Long> roomsByStatus = new LinkedHashMap<>();
        roomsByStatus.put("AVAILABLE", availableRooms);
        roomsByStatus.put("BOOKED", occupiedRooms);
        roomsByStatus.put("MAINTENANCE", maintenanceRooms);

        return AdminStatsResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .confirmedBookings(confirmedBookings)
                .cancelledBookings(cancelledBookings)
                .completedBookings(completedBookings)
                .totalUsers(totalUsers)
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .todayRevenue(todayRevenue)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .todayCheckIns(todayCheckIns)
                .todayCheckOuts(todayCheckOuts)
                .pendingPayments(pendingPayments)
                .revenueByMonth(revenueByMonth)
                .bookingsByMonth(bookingsByMonth)
                .bookingsByStatus(bookingsByStatus)
                .roomsByStatus(roomsByStatus)
                .build();
    }
}
