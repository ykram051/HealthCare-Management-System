package com.university.healthcenter.service;

import com.university.healthcenter.strategies.emails.EmailStrategy;

public interface EmailSenderService {

    void setEmailStrategy(EmailStrategy emailStrategy);
    void sendEmail(String to, String subject, String body);
    
}
