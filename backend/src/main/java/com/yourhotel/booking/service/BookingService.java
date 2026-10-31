package com.yourhotel.booking.service;

import com.yourhotel.booking.dto.request.BookingRequest;
import com.yourhotel.booking.dto.response.BookingResponse;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);
    List<BookingResponse> getUserBookings(String email);
    List<BookingResponse> getAllBookings();
    void cancelBooking(Long id, String userEmail);
    void updateBookingStatus(Long id, String status);
    
    // 🚀 Admin Methods
    BookingResponse adminCreateBooking(BookingRequest request, Long userId); // <-- Added Long userId
    void confirmBooking(Long id);
    void adminCancelBooking(Long id);
}
