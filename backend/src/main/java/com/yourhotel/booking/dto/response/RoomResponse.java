package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String roomType;
    private String description;
    private BigDecimal price;
    private Integer capacity;
    private Integer floor;
    private String status;
    private String imageUrl;

    public static RoomResponse fromEntity(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType().getName())
                .description(room.getRoomType().getDescription())
                .price(room.getRoomType().getBasePrice())
                .capacity(room.getRoomType().getCapacity())
                .floor(room.getFloor())
                .status(room.getStatus().name())
                .imageUrl(room.getImageUrl())
                .build();
    }
}
