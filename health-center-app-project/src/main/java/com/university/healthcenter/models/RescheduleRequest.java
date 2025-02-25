package com.university.healthcenter.models;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RescheduleRequest {

    @NotBlank(message = "New date is required.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the format yyyy-MM-dd.")
    private LocalDate newDate;

    @NotBlank(message = "New time is required.")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in the format HH:mm.")
    private LocalTime newTime;

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewTime() {
        return newTime;
    }

    public void setNewTime(LocalTime newTime) {
        this.newTime = newTime;
    }
}

