package com.university.healthcenter.serviceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.healthcenter.entities.Medicine;
import com.university.healthcenter.entities.Treatment;
import com.university.healthcenter.repository.TreatmentRepository;
import com.university.healthcenter.service.EmailSenderService;
import com.university.healthcenter.service.ReminderService;
import com.university.healthcenter.strategies.emails.EmailStrategy;
import com.university.healthcenter.strategies.emails.MedicineReminderEmailStrategy;

@Service
public class ReminderServiceImpl implements ReminderService{

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public void sendReminders() {
        List<Treatment> treatments = treatmentRepository.findAll();

        for(Treatment treatment:treatments) {
            if(treatment.isOngoing())
                for(Medicine medicine : treatment.getMedicines()) {
                if(shouldSendReminder(medicine)) {
                    EmailStrategy emailStrategy = new MedicineReminderEmailStrategy();
                    emailSenderService.setEmailStrategy(emailStrategy);
                    emailSenderService.sendEmail(treatment.getStudent().getEmail()
                                                , treatment.getName()
                                                , "You should take the medicine: " + medicine.getMedicationName());  
                    }
                }
        }
    }

    private boolean shouldSendReminder(Medicine medicine) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if(today.isBefore(medicine.getStartDate()) || today.isAfter(medicine.getEndDate())) {
            return false;
        }

        int howOften = medicine.getHowOften();
        int hour = now.getHour();

        if (howOften == 1 && hour == 9) return true; // Once at 9 AM
        // Twice: 9 AM and 5 PM
        if (howOften == 2 && (hour == 9 || hour == 17)) return true;
        // Thrice: 9 AM, 2 PM, 9 PM

        return howOften == 3 && (hour == 9 || hour == 14 || hour == 21);

    }
    
}
