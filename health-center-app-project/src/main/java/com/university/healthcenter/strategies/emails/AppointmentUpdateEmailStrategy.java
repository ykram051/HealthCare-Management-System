package com.university.healthcenter.strategies.emails;

import org.springframework.mail.SimpleMailMessage;

public class AppointmentUpdateEmailStrategy implements EmailStrategy {

    @Override
    public SimpleMailMessage prepareEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Update: " + subject);
        message.setText("Dear Patient,\n\n" +
                      "Your appointment has been updated:\n" +
                      body + "\n\n" +
                      "Please make note of these changes.\n\n" +
                      "Best regards,\nHealth Center Team");
        message.setFrom("ybl563425@gmail.com");
        return message;
    }
    
}
