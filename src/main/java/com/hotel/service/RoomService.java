package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoomAvailability(Long id, boolean available) {
        Room room = getRoomById(id);
        room.setAvailable(available);
        return roomRepository.save(room);
    }
}