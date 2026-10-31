package com.yourhotel.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestStatsResponse {
    private long totalGuests;
    private long activeGuests;
    private long inactiveGuests;
    private long adminCount;
    private long currentlyStaying;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageSpendPerGuest;
}
