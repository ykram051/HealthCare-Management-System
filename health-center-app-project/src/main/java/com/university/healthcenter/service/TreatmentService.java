package com.university.healthcenter.service;

import java.util.List;

import com.university.healthcenter.models.MedicineModel;
import com.university.healthcenter.models.TreatmentModel;

public interface TreatmentService {
    List<TreatmentModel> getAllTreatments();
    TreatmentModel getTreatmentById(Integer id);
    TreatmentModel createTreatment(TreatmentModel treatmentModel);
    void deleteTreatment(Integer id);
    TreatmentModel updateTreatment(Integer id, TreatmentModel treatmentModel);
    List<MedicineModel> getMedicinesByTreatmentId(Integer treatmentId);

}
