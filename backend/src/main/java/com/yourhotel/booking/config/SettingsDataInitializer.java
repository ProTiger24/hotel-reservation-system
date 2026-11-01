package com.yourhotel.booking.config;

import com.yourhotel.booking.entity.Setting;
import com.yourhotel.booking.repository.SettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SettingsDataInitializer implements CommandLineRunner {

    private final SettingRepository settingRepository;

    public SettingsDataInitializer(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // চেক করা হচ্ছে ডেটাবেস খালি কিনা
        if (settingRepository.count() == 0) {
            List<Setting> defaultSettings = Arrays.asList(
                // GENERAL
                Setting.builder().key("hotel_name").value("HotelReserve").category("GENERAL").dataType("STRING").label("Hotel Name").description("The name of your hotel").build(),
                Setting.builder().key("contact_email").value("support@hotelreserve.com").category("GENERAL").dataType("STRING").label("Contact Email").build(),
                Setting.builder().key("currency").value("BDT").category("GENERAL").dataType("STRING").label("Currency").build(),
                Setting.builder().key("timezone").value("Asia/Dhaka").category("GENERAL").dataType("STRING").label("Timezone").build(),
                
                // BOOKING
                Setting.builder().key("check_in_time").value("14:00").category("BOOKING").dataType("STRING").label("Check-in Time").build(),
                Setting.builder().key("check_out_time").value("12:00").category("BOOKING").dataType("STRING").label("Check-out Time").build(),
                Setting.builder().key("booking_confirmation_mode").value("AUTO").category("BOOKING").dataType("STRING").label("Confirmation Mode").build(),
                
                // HOTEL_OPERATIONS
                Setting.builder().key("housekeeping_enabled").value("true").category("HOTEL_OPERATIONS").dataType("BOOLEAN").label("Enable Housekeeping").build(),
                
                // PAYMENT
                Setting.builder().key("bkash_mode").value("SANDBOX").category("PAYMENT").dataType("STRING").label("bKash Mode").build(),
                Setting.builder().key("enable_bkash").value("true").category("PAYMENT").dataType("BOOLEAN").label("Enable bKash").build(),
                Setting.builder().key("enable_cash").value("true").category("PAYMENT").dataType("BOOLEAN").label("Enable Cash Payment").build(),
                
                // NOTIFICATION
                Setting.builder().key("email_notifications").value("true").category("NOTIFICATION").dataType("BOOLEAN").label("Email Notifications").build(),
                
                // SECURITY
                Setting.builder().key("password_min_length").value("6").category("SECURITY").dataType("NUMBER").label("Password Min Length").build(),
                
                // INVOICE
                Setting.builder().key("vat_percentage").value("5").category("INVOICE").dataType("NUMBER").label("VAT %").build(),
                Setting.builder().key("invoice_prefix").value("INV-").category("INVOICE").dataType("STRING").label("Invoice Prefix").build(),
                
                // APPEARANCE
                Setting.builder().key("primary_color").value("#C7A46A").category("APPEARANCE").dataType("STRING").label("Primary Color").build(),
                Setting.builder().key("theme_mode").value("LIGHT").category("APPEARANCE").dataType("STRING").label("Theme Mode").build(),
                
                // INTEGRATIONS
                Setting.builder().key("google_maps_api_key").value("").category("INTEGRATIONS").dataType("STRING").label("Google Maps API Key").build(),
                
                // SYSTEM
                Setting.builder().key("maintenance_mode").value("false").category("SYSTEM").dataType("BOOLEAN").label("Maintenance Mode").build(),
                Setting.builder().key("app_version").value("1.0.0").category("SYSTEM").dataType("STRING").label("App Version").build()
            );
            
            settingRepository.saveAll(defaultSettings);
            System.out.println("✅ Default settings populated successfully in the database!");
        }
    }
}
