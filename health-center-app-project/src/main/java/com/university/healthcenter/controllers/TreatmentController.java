package com.university.healthcenter.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.healthcenter.models.MedicineModel;
import com.university.healthcenter.models.TreatmentModel;
import com.university.healthcenter.service.TreatmentService;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping
    public ResponseEntity<List<TreatmentModel>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentModel> getTreatmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @PostMapping
    public ResponseEntity<TreatmentModel> createTreatment(@RequestBody TreatmentModel treatmentModel) {
        return ResponseEntity.ok(treatmentService.createTreatment(treatmentModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Integer id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentModel> updateTreatment(
            @PathVariable Integer id,
            @RequestBody TreatmentModel treatmentModel) {
        TreatmentModel updatedTreatment = treatmentService.updateTreatment(id, treatmentModel);
        return ResponseEntity.ok(updatedTreatment);
    }

    /**
     * Get all medicines for a given treatment ID.
     */
    @GetMapping("/{id}/medicines")
    public ResponseEntity<List<MedicineModel>> getMedicinesByTreatmentId(@PathVariable Integer id) {
        List<MedicineModel> medicines = treatmentService.getMedicinesByTreatmentId(id);
        return ResponseEntity.ok(medicines);
    }
}
