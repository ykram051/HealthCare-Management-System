package com.university.healthcenter.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.university.healthcenter.service.EmailSenderService;
import com.university.healthcenter.strategies.emails.EmailStrategy;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired 
    private EmailStrategy emailStrategy;


    @Override
    public void setEmailStrategy(EmailStrategy emailStrategy) {
        this.emailStrategy = emailStrategy;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (emailStrategy == null) {
            throw new IllegalStateException("EmailStrategy is not set");
        }
        
        SimpleMailMessage message = emailStrategy.prepareEmail(to, subject, body);
        mailSender.send(message);
    }
}
