package com.yourhotel.booking.service;

import com.yourhotel.booking.dto.response.RoomResponse;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    List<RoomResponse> getAvailableRooms();
    RoomResponse getRoomById(Long id);
}
