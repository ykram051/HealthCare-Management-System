package com.university.healthcenter.serviceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.healthcenter.entities.Appointment;
import com.university.healthcenter.entities.HealthStaff;
import com.university.healthcenter.entities.Student;
import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.observers.appointment.HealthStaffAppointmentObserver;
import com.university.healthcenter.observers.appointment.StudentAppointmentObserver;
import com.university.healthcenter.repository.AppointmentRepository;
import com.university.healthcenter.repository.HealthStaffRepository;
import com.university.healthcenter.repository.StudentRepository;
import com.university.healthcenter.service.AppointmentService;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HealthStaffRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StudentAppointmentObserver studentObserver;

    @Autowired
    private HealthStaffAppointmentObserver healthStaffObserver; 

    @Override
    public List<AppointmentModel> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentModel getAppointmentById(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        return modelMapper.map(appointment, AppointmentModel.class);
    }

    @Override
    public AppointmentModel createAppointment(AppointmentModel appointmentModel) {
        boolean exists = appointmentRepository.existsByDoctorUserIdAndAppointmentDateAndAppointmentTime(
                appointmentModel.getDoctorId(),
                appointmentModel.getAppointmentDate(),
                appointmentModel.getAppointmentTime());

        if (exists) {
            throw new RuntimeException("The selected appointment slot is already booked.");
        }

        Appointment appointment = modelMapper.map(appointmentModel, Appointment.class);

        Student student = studentRepository.findById(appointmentModel.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + appointmentModel.getStudentId()));
        appointment.setStudent(student);

        HealthStaff doctor = doctorRepository.findById(appointmentModel.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + appointmentModel.getDoctorId()));
        appointment.setDoctor(doctor);

        appointment.setStatus("UPCOMING");
        Appointment savedAppointment = appointmentRepository.save(appointment);

        studentObserver.onAppointmentCreated(savedAppointment);
        healthStaffObserver.onAppointmentCreated(savedAppointment);

        return modelMapper.map(savedAppointment, AppointmentModel.class);
    }

    @Override
    public List<AppointmentModel> getAppointmentsByStudentId(int studentId) {
        return appointmentRepository.findByStudentUserId(studentId)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentModel> getAppointmentsByDoctorId(int doctorId) {
        return appointmentRepository.findByDoctorUserId(doctorId)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        
        if (appointment.isPresent()) {
            studentObserver.onAppointmentDeleted(appointment.get());
            healthStaffObserver.onAppointmentDeleted(appointment.get());
        }
        appointmentRepository.deleteById(appointmentId);
    }

    @Override
    public List<AppointmentModel> getAppointmentsByDoctorIdAndDate(int doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorUserIdAndAppointmentDate(doctorId, date)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void rescheduleAppointment(int studentId, int appointmentId, LocalDate newDate, LocalTime newTime) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (appointment.getStudent().getUserId() != studentId) {
            throw new IllegalArgumentException("You are not authorized to reschedule this appointment.");
        }

        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot schedule an appointment in the past.");
        }

        if (appointmentRepository.existsByDoctorUserIdAndAppointmentDateAndAppointmentTime(
                appointment.getDoctor().getUserId(), newDate, newTime)) {
            throw new IllegalArgumentException("The selected time slot is already booked.");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);

        studentObserver.onAppointmentUpdated(appointment);
        healthStaffObserver.onAppointmentUpdated(appointment);;

        appointmentRepository.save(appointment);
    }

    @Override
    public void saveAppointment(AppointmentModel appointmentModel) {
        if (appointmentModel == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        Appointment appointment = modelMapper.map(appointmentModel, Appointment.class);
        studentObserver.onAppointmentCreated(appointment);
        appointmentRepository.save(appointment);
    }
}
