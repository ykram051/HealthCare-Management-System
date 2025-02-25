package com.university.healthcenter.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.university.healthcenter.entities.Medicine;
import com.university.healthcenter.entities.Student;
import com.university.healthcenter.entities.Treatment;
import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.StudentModel;
import com.university.healthcenter.models.TreatmentModel;
import com.university.healthcenter.repository.AppointmentRepository;
import com.university.healthcenter.repository.MedicineRepository;
import com.university.healthcenter.repository.StudentRepository;
import com.university.healthcenter.repository.TreatmentRepository;
import com.university.healthcenter.service.StudentService;
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private MedicineRepository medicineRepository;
    
        @Override
        public List<StudentModel> getAllStudents() {
            return studentRepository.findAll()
                    .stream()
                    .map(student -> modelMapper.map(student, StudentModel.class))
                    .collect(Collectors.toList());
        }
    
        @Override
        public Optional<StudentModel> getStudentById(int studentId) {
            return studentRepository.findById(studentId)
                    .map(student -> modelMapper.map(student, StudentModel.class));
        }
    
        @Override
        public StudentModel saveStudent(StudentModel studentModel) {
            Student student = modelMapper.map(studentModel, Student.class);
            Student savedStudent = studentRepository.save(student);
            return modelMapper.map(savedStudent, StudentModel.class);
        }
    
        @Override
        public void deleteStudent(int studentId) {
            studentRepository.deleteById(studentId);
        }
    
        @Override
        public StudentModel updateStudentProfile(int studentId, StudentModel studentModel) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
            modelMapper.map(studentModel, student);
            Student updatedStudent = studentRepository.save(student);
            return modelMapper.map(updatedStudent, StudentModel.class);
        }
    
        @Override
        public List<AppointmentModel> getAppointmentsByStudentId(int studentId) {
            return appointmentRepository.findByStudentUserId(studentId)
                    .stream()
                    .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                    .collect(Collectors.toList());
        }
    
        @Override
        public List<StudentModel> findStudentsByAllergy(String allergy) {
            return studentRepository.findByAllergiesContaining(allergy)
                    .stream()
                    .map(student -> modelMapper.map(student, StudentModel.class))
                    .collect(Collectors.toList());
        }
    
        @Override
        public StudentModel createStudent(StudentModel studentModel) {
            Student student = modelMapper.map(studentModel, Student.class);
            Student savedStudent = studentRepository.save(student);
            return modelMapper.map(savedStudent, StudentModel.class);
        }
    
        @Override
        public void createTreatmentForStudent(int studentId, TreatmentModel treatmentModel) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
    
            Treatment treatment = new Treatment();
            treatment.setName(treatmentModel.getName());
            treatment.setStudent(student);
    
            // Map medicines if provided
            List<Medicine> medicines = treatmentModel.getMedicines().stream().map(medicineModel -> {
                Medicine medicine = new Medicine();
                medicine.setMedicationName(medicineModel.getMedicationName());
                medicine.setHowOften(medicineModel.getHowOften());
                medicine.setStartDate(medicineModel.getStartDate());
                medicine.setEndDate(medicineModel.getEndDate());
                medicine.setTreatment(treatment);
                return medicine;
            }).collect(Collectors.toList());
    
            treatment.setMedicines(medicines);
    
            treatmentRepository.save(treatment);
        }
    
        @Override
    public List<TreatmentModel> getTreatmentsByStudentId(int studentId) {
    
                // Fetch treatments and map to TreatmentModel
                return treatmentRepository.findByStudentUserId(studentId)
                .stream()
                .map(treatment -> modelMapper.map(treatment, TreatmentModel.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public void deleteTreatmentForStudent(Integer studentId, Integer treatmentId) {
        // Fetch the treatment
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));
    
        // Check if the student associated with the treatment matches the provided studentId
        if (treatment.getStudent() == null || !treatment.getStudent().equals(studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found")))) {
            throw new RuntimeException("Treatment does not belong to the specified student");
        }
    
        // Delete associated medicines
        medicineRepository.deleteByTreatmentId(treatmentId);
        // Delete the treatment
        treatmentRepository.deleteByStudentUserIdAndId(studentId,treatmentId);
}
@Override
public List<Medicine> getAllMedicinesByStudentId(int studentId) {
    List<Treatment> treatments = treatmentRepository.findByStudentUserId(studentId);
    return treatments.stream()
                     .flatMap(treatment -> treatment.getMedicines().stream())
                     .collect(Collectors.toList());
}

@Override
@Transactional
public void deleteMedicineForStudent(int studentId, int treatmentId, int medicineId) {
    Medicine medicine = medicineRepository.findById(medicineId)
            .orElseThrow(() -> new RuntimeException("Medicine not found"));

    // Validate that the medicine belongs to the specified treatment and student
    if (medicine.getTreatment() == null || 
        medicine.getTreatment().getId() != treatmentId || 
        medicine.getTreatment().getStudent().getUserId() != studentId) {
        throw new RuntimeException("Medicine does not belong to the specified treatment or student");
    }

    medicineRepository.deleteById(medicineId);
}

    @Override
public TreatmentModel createTreatmentFromPrescription(MultipartFile file, Integer studentId) throws Exception {
    // Extract text from the PDF
    PDDocument document = PDDocument.load(file.getInputStream());
    PDFTextStripper pdfStripper = new PDFTextStripper();
    String extractedText = pdfStripper.getText(document);
    document.close();

    // Parse the extracted text to create a TreatmentModel
    Treatment treatment = parsePrescription(extractedText, studentId);
    Treatment savedTreatment = treatmentRepository.save(treatment);

    return modelMapper.map(savedTreatment, TreatmentModel.class);
}

private Treatment parsePrescription(String text, Integer studentId) {
    // Create a new Treatment object
    Treatment treatment = new Treatment();
    treatment.setName("Prescription-based Treatment");

    // Retrieve the student from the repository
    Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
    treatment.setStudent(student);

    // Initialize medicines list
    List<Medicine> medicines = new ArrayList<>();

    // Parse the prescription text (example logic)
    String[] lines = text.split("\n");
    for (String line : lines) {
        if (line.toLowerCase().contains("medicine:")) {
            Medicine medicine = new Medicine();

            // Extract medication name
            String medicationName = line.split(":")[1].trim();
            medicine.setMedicationName(medicationName);

            // Set default dosage frequency (update as needed)
            medicine.setHowOften(3); // Example: 3 times a day

            // Set default start and end dates
            medicine.setStartDate(LocalDate.now());
            medicine.setEndDate(LocalDate.now().plusDays(7)); // Example: 7-day treatment duration

            // Set relationship with the treatment
            medicine.setTreatment(treatment);

            medicines.add(medicine);
        }
    }

    treatment.setMedicines(medicines);
    return treatment;
}



}