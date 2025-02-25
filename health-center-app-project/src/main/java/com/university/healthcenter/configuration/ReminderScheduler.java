package com.university.healthcenter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.university.healthcenter.service.ReminderService;

@Component
public class ReminderScheduler  {

    @Autowired
    private ReminderService reminderService;

    // executes every hour
    @Scheduled(cron="0 0 * * * *")
    public void scheduleReminders() {
        System.out.println("Cron triggered");
        reminderService.sendReminders();
    }

    
}
