package com.yourhotel.booking.service.impl;

import com.yourhotel.booking.dto.request.BookingRequest;
import com.yourhotel.booking.dto.response.BookingResponse;
import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.Room;
import com.yourhotel.booking.entity.User;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.RoomRepository;
import com.yourhotel.booking.repository.UserRepository;
import com.yourhotel.booking.service.BookingService;
import com.yourhotel.booking.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository,
                              EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return createBookingForUser(request, user);
    }

    @Override
    @Transactional
    public BookingResponse adminCreateBooking(BookingRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return createBookingForUser(request, user);
    }

    private BookingResponse createBookingForUser(BookingRequest request, User user) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
            request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new BadRequestException("Check-out must be after check-in");
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                room.getId(), request.getCheckIn(), request.getCheckOut());

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Room is not available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal roomPrice = room.getRoomType() != null ? room.getRoomType().getBasePrice() : BigDecimal.ZERO;
        BigDecimal totalPrice = roomPrice.multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guestCount(request.getGuestCount())
                .specialRequest(request.getSpecialRequest())
                .status(Booking.BookingStatus.PENDING)
                .paymentStatus(Booking.PaymentStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        Booking saved = bookingRepository.save(booking);

        // 🚀 Send Email
        try {
            emailService.sendBookingConfirmation(
                user.getEmail(),
                user.getName(),
                saved.getId(),
                "Room: " + room.getRoomNumber() + "\nCheck-in: " + saved.getCheckIn() + "\nCheck-out: " + saved.getCheckOut()
            );
        } catch (Exception e) {
            System.err.println("Email Error: " + e.getMessage());
        }

        return BookingResponse.fromEntity(saved);
    }

    @Override
    public List<BookingResponse> getUserBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new BadRequestException("Not authorized to cancel this booking");
        }
        
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        try {
            booking.setStatus(Booking.BookingStatus.valueOf(status.toUpperCase()));
            bookingRepository.save(booking);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public void confirmBooking(Long id) {
        updateBookingStatus(id, "CONFIRMED");
    }

    @Override
    @Transactional
    public void adminCancelBooking(Long id) {
        updateBookingStatus(id, "CANCELLED");
    }
}
