package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/payments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {

    private final BookingRepository bookingRepository;

    public AdminPaymentController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllPayments() {
        List<Map<String, Object>> payments = bookingRepository.findAllWithDetails().stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getId());
                    map.put("guestName", b.getUser() != null ? b.getUser().getName() : "Guest");
                    map.put("roomNumber", b.getRoom() != null ? b.getRoom().getRoomNumber() : "—");
                    map.put("amount", b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO);
                    map.put("paymentStatus", b.getPaymentStatus() != null ? b.getPaymentStatus().name() : "PENDING");
                    map.put("bookingDate", b.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    // Update payment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        try {
            booking.setPaymentStatus(Booking.PaymentStatus.valueOf(status.toUpperCase()));
            bookingRepository.save(booking);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated", null));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }
}
