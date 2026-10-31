package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.BookingResponse;
import com.yourhotel.booking.service.CheckinService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/checkin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCheckinController {

    private final CheckinService checkinService;

    public AdminCheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getTodayBookings() {
        List<BookingResponse> list = checkinService.getTodayBookings().stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    
    @GetMapping("/in-house")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getInHouseBookings() {
        List<BookingResponse> list = checkinService.getInHouseBookings().stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<Void>> checkIn(@PathVariable Long id) {
        checkinService.checkIn(id);
        return ResponseEntity.ok(ApiResponse.success("Guest checked in successfully. Room is now OCCUPIED.", null));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<Void>> checkOut(@PathVariable Long id) {
        checkinService.checkOut(id);
        return ResponseEntity.ok(ApiResponse.success("Guest checked out successfully. Room is now DIRTY (Housekeeping required).", null));
    }
}
