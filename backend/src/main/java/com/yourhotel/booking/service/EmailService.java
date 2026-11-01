package com.yourhotel.booking.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${RESEND_API_KEY}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    // Core method to send email using Resend
    public void sendEmail(String to, String subject, String htmlContent) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("✅ Email sent successfully! ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    // 🚀 আপনার প্রজেক্টে আগে থেকে থাকা মেথডগুলো আবার যোগ করা হলো

    public void sendPaymentSuccess(String to, String guestName, Long bookingId, String transactionId) {
        String subject = "Payment Successful - HotelReserve";
        String html = "<h3>Dear " + guestName + ",</h3>"
                + "<p>Your payment for Booking ID: <strong>#" + bookingId + "</strong> was successful.</p>"
                + "<p>Transaction ID: " + transactionId + "</p>"
                + "<p>Thank you for choosing HotelReserve!</p>";
        sendEmail(to, subject, html);
    }

    public void sendBookingConfirmation(String to, String guestName, Long bookingId, String roomNumber) {
        String subject = "Booking Confirmed - HotelReserve";
        String html = "<h3>Dear " + guestName + ",</h3>"
                + "<p>Your booking is confirmed! Booking ID: <strong>#" + bookingId + "</strong></p>"
                + "<p>Room Number: " + roomNumber + "</p>"
                + "<p>We look forward to hosting you.</p>";
        sendEmail(to, subject, html);
    }
}
