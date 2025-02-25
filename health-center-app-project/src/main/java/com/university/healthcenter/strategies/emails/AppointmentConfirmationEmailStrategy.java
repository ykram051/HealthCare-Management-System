package com.university.healthcenter.strategies.emails;

import org.springframework.mail.SimpleMailMessage;


public class AppointmentConfirmationEmailStrategy implements EmailStrategy {

    @Override
    public SimpleMailMessage prepareEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Confirmation: " + subject);
        message.setText("""
                        Greetings,
                        
                        Your appointment has been confirmed:
                        """ +
                      body + "\n\n" +
                      "Best regards,\nHealth Center Team");
        message.setFrom("ybl563425@gmail.com");
        return message;
    }
    
}