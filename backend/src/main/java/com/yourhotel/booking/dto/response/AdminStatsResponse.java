package com.yourhotel.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private long completedBookings;
    private long totalUsers;
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal todayRevenue;
    
    private double occupancyRate;
    private double cancellationRate;
    
    // ✅ NEW fields
    private long todayCheckIns;
    private long todayCheckOuts;
    private long pendingPayments;
    
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, Long> bookingsByMonth;
    private Map<String, Long> bookingsByStatus;
    private Map<String, Long> roomsByStatus;
}
