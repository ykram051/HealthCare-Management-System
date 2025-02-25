package com.university.healthcenter.models;

import java.time.LocalDate;

public class MedicineModel {

    private Integer id;
    private String medicationName;
    private Integer howOften;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer treatmentId;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public Integer getHowOften() {
        return howOften;
    }

    public void setHowOften(Integer howOften) {
        this.howOften = howOften;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }



   
}
