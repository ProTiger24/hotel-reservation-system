package com.yourhotel.booking.service;

import com.yourhotel.booking.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SettingRepository settingRepository;

    private boolean isEmailEnabled() {
        return settingRepository.findById("email_notifications")
                .map(s -> "true".equalsIgnoreCase(s.getValue()))
                .orElse(false);
    }

    public void sendEmail(String to, String subject, String body) {
        if (!isEmailEnabled()) {
            System.out.println("⚠️ Email notifications are disabled in settings.");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    public void sendBookingConfirmation(String toEmail, String guestName, Long bookingId, String details) {
        String subject = "Booking Confirmed - HotelReserve";
        String body = "Dear " + guestName + ",\n\n" +
                "Your booking (#" + bookingId + ") has been successfully confirmed.\n\n" +
                "Details:\n" + details + "\n\n" +
                "Thank you for choosing HotelReserve!\n\n" +
                "Best Regards,\nHotelReserve Team";
        sendEmail(toEmail, subject, body);
    }

    public void sendPaymentSuccess(String toEmail, String guestName, Long bookingId, String amount) {
        String subject = "Payment Successful - HotelReserve";
        String body = "Dear " + guestName + ",\n\n" +
                "We have received your payment of ৳" + amount + " for booking #" + bookingId + ".\n" +
                "Your invoice is available in your dashboard.\n\n" +
                "Thank you!\nHotelReserve Team";
        sendEmail(toEmail, subject, body);
    }
}
