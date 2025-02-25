package com.university.healthcenter.strategies.emails;

import org.springframework.mail.SimpleMailMessage;

public class MedicineReminderEmailStrategy implements EmailStrategy {

    @Override
    public SimpleMailMessage prepareEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Medicine Reminder: " + subject);
        message.setText("""
                        Dear Patient,
                        
                        This is a reminder for your medication:
                        """ +
                      body + "\n\n" +
                      "Important: Please follow your prescribed dosage.\n\n" +
                      "Best regards,\nHealth Center Team");
        message.setFrom("ybl563425@gmail.com");
        return message;
    }
    
}

