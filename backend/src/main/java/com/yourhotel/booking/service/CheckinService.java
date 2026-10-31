package com.yourhotel.booking.service;

import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheckinService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public CheckinService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only CONFIRMED bookings can be checked in");
        }

        LocalDate today = LocalDate.now();
        if (booking.getCheckIn().isAfter(today)) {
            throw new BadRequestException("Cannot check-in before the scheduled date (" + booking.getCheckIn() + ")");
        }

        Room room = booking.getRoom();
        if (room.getStatus() != Room.RoomStatus.AVAILABLE && room.getStatus() != Room.RoomStatus.BOOKED) {
            throw new BadRequestException("Room is not available for check-in. Current status: " + room.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        room.setStatus(Room.RoomStatus.OCCUPIED);

        bookingRepository.save(booking);
        roomRepository.save(room);
    }

    @Transactional
    public void checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Guest is not checked in");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        
        Room room = booking.getRoom();
        // 🚀 Professional Workflow: Room goes to DIRTY, not AVAILABLE immediately
        try {
            room.setStatus(Room.RoomStatus.valueOf("DIRTY"));
        } catch (IllegalArgumentException e) {
            room.setStatus(Room.RoomStatus.AVAILABLE); // Fallback
        }

        bookingRepository.save(booking);
        roomRepository.save(room);
    }

    // Fetch Today's Bookings efficiently with JOIN FETCH to prevent LazyInit error
    
    public List<Booking> getInHouseBookings() {
        return bookingRepository.findByStatus(Booking.BookingStatus.CHECKED_IN);
    }

    public List<Booking> getTodayBookings() {
        return bookingRepository.findTodayBookings(LocalDate.now());
    }
}
