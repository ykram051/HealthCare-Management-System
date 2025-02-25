package com.university.healthcenter.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.healthcenter.entities.Medicine;
import com.university.healthcenter.entities.Student;
import com.university.healthcenter.entities.Treatment;
import com.university.healthcenter.models.MedicineModel;
import com.university.healthcenter.models.TreatmentModel;
import com.university.healthcenter.repository.StudentRepository;
import com.university.healthcenter.repository.TreatmentRepository;
import com.university.healthcenter.service.TreatmentService;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TreatmentModel> getAllTreatments() {
        return treatmentRepository.findAll()
                .stream()
                .map(treatment -> modelMapper.map(treatment, TreatmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentModel getTreatmentById(Integer id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));
        return modelMapper.map(treatment, TreatmentModel.class);
    }

    @Override
    public TreatmentModel createTreatment(TreatmentModel treatmentModel) {
        Treatment treatment = modelMapper.map(treatmentModel, Treatment.class);

        Student student = studentRepository.findById(treatmentModel.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + treatmentModel.getStudentId()));
        treatment.setStudent(student);

        for (Medicine medicine : treatment.getMedicines()) {
            medicine.setTreatment(treatment);
        }

        Treatment savedTreatment = treatmentRepository.save(treatment);
        return modelMapper.map(savedTreatment, TreatmentModel.class);
    }

    @Override
    public void deleteTreatment(Integer id) {
        treatmentRepository.deleteById(id);
    }

    @Override
public TreatmentModel updateTreatment(Integer id, TreatmentModel treatmentModel) {
    Treatment treatment = treatmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Treatment not found"));

    // Update treatment fields
    treatment.setName(treatmentModel.getName());
    treatment.getMedicines().clear(); // Clear existing medicines

    // Map medicines from the request
    treatmentModel.getMedicines().forEach(medicineModel -> {
        Medicine medicine = modelMapper.map(medicineModel, Medicine.class);
        medicine.setTreatment(treatment);
        treatment.getMedicines().add(medicine);
    });

    Treatment updatedTreatment = treatmentRepository.save(treatment); // Save changes
    return modelMapper.map(updatedTreatment, TreatmentModel.class); // Return updated model
}
@Override
    public List<MedicineModel> getMedicinesByTreatmentId(Integer treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new RuntimeException("Treatment not found with ID: " + treatmentId));

        // Map medicines to MedicineModel
        return treatment.getMedicines()
                .stream()
                .map(medicine -> modelMapper.map(medicine, MedicineModel.class))
                .collect(Collectors.toList());
    }

}
