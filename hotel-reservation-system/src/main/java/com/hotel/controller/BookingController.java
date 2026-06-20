package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/email/{email}")
    public List<Booking> getBookingsByEmail(@PathVariable String email) {
        return bookingService.getBookingsByEmail(email);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PutMapping("/{id}/checkin")
    public Booking checkIn(@PathVariable Long id) {
        return bookingService.checkIn(id);
    }

    @PutMapping("/{id}/checkout")
    public Booking checkOut(@PathVariable Long id) {
        return bookingService.checkOut(id);
    }

    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}