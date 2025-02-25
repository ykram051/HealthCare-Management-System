package com.university.healthcenter.strategies.emails;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCancelingEmailStrategy implements EmailStrategy {
    @Override
    public SimpleMailMessage prepareEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Cancellation: " + subject);
        message.setText("Greetings,\n\n" +
                      "Your appointment has been cancelled:\n" +
                      body + "\n\n" +
                      "If you didn't request this cancellation, please contact us immediately.\n\n" +
                      "Best regards,\nHealth Center Team");
        message.setFrom("ybl563425@gmail.com");
        return message;
    }
}
