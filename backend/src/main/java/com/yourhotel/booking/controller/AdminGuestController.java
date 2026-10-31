package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.BookingResponse;
import com.yourhotel.booking.dto.response.GuestResponse;
import com.yourhotel.booking.dto.response.GuestStatsResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.User;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/guests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGuestController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public AdminGuestController(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<GuestStatsResponse>> getStats() {
        List<User> users = userRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAllWithDetails();
        LocalDate today = LocalDate.now();

        long totalGuests = users.size();
        long activeGuests = users.stream().filter(u -> Boolean.TRUE.equals(u.getEnabled())).count();
        long inactiveGuests = totalGuests - activeGuests;
        long adminCount = users.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();

        Set<Long> stayingUserIds = new HashSet<>();
        for (Booking b : allBookings) {
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED
                && !today.isBefore(b.getCheckIn())
                && today.isBefore(b.getCheckOut())) {
                stayingUserIds.add(b.getUser().getId());
            }
        }

        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED
                          || b.getStatus() == Booking.BookingStatus.COMPLETED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgSpend = totalGuests > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalGuests), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        GuestStatsResponse stats = GuestStatsResponse.builder()
                .totalGuests(totalGuests)
                .activeGuests(activeGuests)
                .inactiveGuests(inactiveGuests)
                .adminCount(adminCount)
                .currentlyStaying(stayingUserIds.size())
                .totalBookings(allBookings.size())
                .totalRevenue(totalRevenue)
                .averageSpendPerGuest(avgSpend)
                .build();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GuestResponse>>> getAllGuests() {
        List<User> users = userRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAllWithDetails();
        LocalDate today = LocalDate.now();

        List<GuestResponse> guests = users.stream().map(u -> {
            GuestResponse r = GuestResponse.fromEntity(u);
            List<Booking> userBookings = allBookings.stream()
                    .filter(b -> b.getUser().getId().equals(u.getId()))
                    .collect(Collectors.toList());

            long completed = userBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
            long cancelled = userBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
            long upcoming = userBookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.PENDING)
                    .filter(b -> !b.getCheckIn().isBefore(today))
                    .count();

            BigDecimal spent = userBookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED)
                    .map(Booking::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal avg = userBookings.isEmpty() ? BigDecimal.ZERO
                    : spent.divide(BigDecimal.valueOf(userBookings.size()), 2, RoundingMode.HALF_UP);

            boolean staying = userBookings.stream().anyMatch(b ->
                b.getStatus() == Booking.BookingStatus.CONFIRMED
                && !today.isBefore(b.getCheckIn())
                && today.isBefore(b.getCheckOut()));

            Optional<Booking> lastStay = userBookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED || b.getStatus() == Booking.BookingStatus.CONFIRMED)
                    .max(Comparator.comparing(Booking::getCheckOut));

            r.setTotalBookings((long) userBookings.size());
            r.setCompletedBookings(completed);
            r.setCancelledBookings(cancelled);
            r.setUpcomingBookings(upcoming);
            r.setTotalSpent(spent);
            r.setAverageBooking(avg);
            r.setCurrentlyStaying(staying);
            r.setLastStayDate(lastStay.map(b -> b.getCheckOut().atStartOfDay()).orElse(null));

            return r;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(guests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestResponse>> getGuest(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));
        return ResponseEntity.ok(ApiResponse.success(GuestResponse.fromEntity(user)));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getGuestBookings(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Guest not found");
        }
        List<BookingResponse> bookings = bookingRepository.findAllWithDetails().stream()
                .filter(b -> b.getUser().getId().equals(id))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<GuestResponse>> updateStatus(@PathVariable Long id,
                                                                    @RequestParam Boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (user.getRole() == User.Role.ADMIN && !enabled) {
            throw new BadRequestException("Cannot deactivate an admin account");
        }

        user.setEnabled(enabled);
        User saved = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(
                enabled ? "Guest activated" : "Guest deactivated",
                GuestResponse.fromEntity(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestResponse>> updateGuest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        // 🚀 SAFE UPDATE (Added curly braces)
        if (body.containsKey("name")) { user.setName(body.get("name")); }
        if (body.containsKey("phone")) { user.setPhone(body.get("phone")); }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Guest updated", GuestResponse.fromEntity(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGuest(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new BadRequestException("Cannot delete admin accounts");
        }

        boolean hasActive = bookingRepository.findAllWithDetails().stream()
                .anyMatch(b -> b.getUser().getId().equals(id)
                          && (b.getStatus() == Booking.BookingStatus.CONFIRMED
                           || b.getStatus() == Booking.BookingStatus.PENDING));

        if (hasActive) {
            throw new BadRequestException("Cannot delete guest with active bookings. Deactivate instead.");
        }

        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.success("Guest deleted", null));
    }
}
