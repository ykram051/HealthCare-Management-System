package com.university.healthcenter.strategies.emails;

import org.springframework.mail.SimpleMailMessage;


public interface EmailStrategy {
    SimpleMailMessage prepareEmail(String to, String subject, String body);
}