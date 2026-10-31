package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r JOIN FETCH r.roomType WHERE r.status = :status")
    List<Room> findByStatus(Room.RoomStatus status);

    @Query("SELECT r FROM Room r JOIN FETCH r.roomType")
    List<Room> findAllWithRoomType();

    boolean existsByRoomNumber(String roomNumber);
}
