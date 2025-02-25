package com.university.healthcenter.strategies.emails;

import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


@Component
@Primary
public class DefaultEmailStrategy implements EmailStrategy {
    @Override
    public SimpleMailMessage prepareEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("ybl563425@gmail.com");
        return message;
    }
}