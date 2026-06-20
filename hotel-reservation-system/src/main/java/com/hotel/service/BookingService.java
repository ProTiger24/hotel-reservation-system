package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }
    
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    
    @Transactional
    public Booking createBooking(Booking booking) {
        Room room = roomRepository.findById(booking.getRoom().getId())
            .orElseThrow(() -> new RuntimeException("Room not found"));
        
        if (!room.getAvailable()) {
            throw new RuntimeException("Room is not available");
        }
        
        long days = booking.getCheckOut().toEpochDay() - booking.getCheckIn().toEpochDay();
        if (days <= 0) {
            throw new RuntimeException("Check-out must be after check-in");
        }
        
        double total = room.getPrice() * days;
        booking.setTotalAmount(total);
        booking.setAdvancePaid(total * 0.30);
        booking.setRemainingAmount(total * 0.70);
        booking.setPaymentStatus("ADVANCE_PAID");
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(LocalDate.now());
        booking.setIsCheckedIn(false);
        booking.setIsCheckedOut(false);
        
        room.setAvailable(false);
        roomRepository.save(room);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking.getIsCheckedIn()) {
            throw new RuntimeException("Already checked in");
        }
        
        if (!booking.getStatus().equals("CONFIRMED")) {
            throw new RuntimeException("Booking is not confirmed");
        }
        
        LocalDate today = LocalDate.now();
        if (today.isBefore(booking.getCheckIn())) {
            throw new RuntimeException("Cannot check in before check-in date");
        }
        
        booking.setIsCheckedIn(true);
        booking.setCheckInTime(LocalDateTime.now());
        booking.setPaymentStatus("CHECKED_IN");
        booking.setStatus("CHECKED_IN");
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking.getIsCheckedOut()) {
            throw new RuntimeException("Already checked out");
        }
        
        if (!booking.getIsCheckedIn()) {
            throw new RuntimeException("Not checked in yet");
        }
        
        LocalDate today = LocalDate.now();
        if (today.isBefore(booking.getCheckOut())) {
            throw new RuntimeException("Cannot check out before check-out date");
        }
        
        booking.setIsCheckedOut(true);
        booking.setCheckOutTime(LocalDateTime.now());
        booking.setPaymentStatus("COMPLETED");
        booking.setStatus("COMPLETED");
        
        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        
        if (booking.getIsCheckedIn()) {
            throw new RuntimeException("Cannot cancel after check-in");
        }
        
        if (booking.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Booking already cancelled");
        }
        
        booking.setStatus("CANCELLED");
        booking.setPaymentStatus("CANCELLED");
        
        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);
        
        return bookingRepository.save(booking);
    }
}
