package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.BookingRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.BookingResponse;
import com.yourhotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookingService bookingService;

    public AdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 🚀 Get All Bookings
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getAllBookings()));
    }

    // 🚀 Admin Creates a New Booking
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request, 
            @RequestParam Long userId) {
        BookingResponse response = bookingService.adminCreateBooking(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Booking created by admin", response));
    }

    // 🚀 Confirm Booking (Fixed: separated the void method call)
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id); // Call the void method
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully", null)); // Return success message
    }

    // 🚀 Cancel Booking (Fixed: separated the void method call)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable Long id) {
        bookingService.adminCancelBooking(id); // Call the void method
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", null)); // Return success message
    }
}
