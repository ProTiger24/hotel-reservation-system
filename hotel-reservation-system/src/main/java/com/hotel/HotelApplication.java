
package com.hotel;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
        System.out.println("\n🏨 =========================================");
        System.out.println("   HOTEL RESERVATION SYSTEM STARTED");
        System.out.println("=========================================");
        System.out.println("   🌐 http://localhost:8080");
        System.out.println("   📊 MySQL Database: hotel_db");
        System.out.println("=========================================\n");
    }

    @Bean
    CommandLineRunner initDatabase(RoomRepository roomRepository) {
        return args -> {
            if (roomRepository.count() == 0) {
                Room room1 = new Room();
                room1.setType("Standard");
                room1.setPrice(1500.0);
                room1.setCapacity(2);
                room1.setAvailable(true);
                room1.setDescription("Comfortable standard room with essential amenities");
                room1.setAmenities("WiFi, TV, AC, Attached Bathroom");
                roomRepository.save(room1);

                Room room2 = new Room();
                room2.setType("Deluxe");
                room2.setPrice(2500.0);
                room2.setCapacity(3);
                room2.setAvailable(true);
                room2.setDescription("Spacious deluxe room with premium features");
                room2.setAmenities("WiFi, TV, AC, Mini Bar, Bathtub, City View");
                roomRepository.save(room2);

                Room room3 = new Room();
                room3.setType("Suite");
                room3.setPrice(4000.0);
                room3.setCapacity(4);
                room3.setAvailable(true);
                room3.setDescription("Luxurious suite with separate living and dining area");
                room3.setAmenities("WiFi, TV, AC, Mini Bar, Bathtub, Living Room, Kitchenette, Ocean View");
                roomRepository.save(room3);

                System.out.println("✅ 3 rooms initialized!");
            }
        };
    }
}