package com.yourhotel.booking.repository;

import com.yourhotel.booking.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(String category);
}
