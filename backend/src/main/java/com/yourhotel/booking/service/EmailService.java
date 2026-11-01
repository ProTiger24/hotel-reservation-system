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

    // application.properties থেকে API Key নেওয়া হচ্ছে
    public EmailService(@Value("${RESEND_API_KEY}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev") // Resend-এর ফ্রি টিয়ারে এই ইমেইলটি দিতে হবে
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
}
