package com.hospital.management.config;

import com.hospital.management.model.*;
import com.hospital.management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final WardRepository wardRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final NurseRepository nurseRepository;
    private final LabTestRepository labTestRepository;
    private final TestResultRepository testResultRepository;
    private final InvoiceRepository invoiceRepository;

    // We inject the repositories so we can save data
    public DataSeeder(DoctorRepository doctorRepository, PatientRepository patientRepository,
                      AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository,
                      WardRepository wardRepository, RoomRepository roomRepository, BedRepository bedRepository,
                      MedicineRepository medicineRepository, PrescriptionRepository prescriptionRepository,
                      PrescriptionItemRepository prescriptionItemRepository, NurseRepository nurseRepository,
                      LabTestRepository labTestRepository, TestResultRepository testResultRepository,
                      InvoiceRepository invoiceRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.wardRepository = wardRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.medicineRepository = medicineRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.nurseRepository =  nurseRepository;
        this.labTestRepository = labTestRepository;
        this.testResultRepository = testResultRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed data if the database is empty
        if (doctorRepository.count() == 0) {
            System.out.println("🌱 Seeding database with dummy data...");

            // 1. Create Departments
            Department cardio = new Department();
            cardio.setName("Cardiology");
            cardio.setDescription("Heart and vascular system");
            departmentRepository.save(cardio);

            Department emergency = new Department();
            emergency.setName("Emergency");
            emergency.setDescription("Urgent care");
            departmentRepository.save(emergency);

            // 2. Create Wards, Rooms, Beds (Infrastructure)
            Ward westWing = new Ward();
            westWing.setName("West Wing");
            westWing.setCapacity(20);
            westWing.setDepartment(cardio);
            wardRepository.save(westWing);

            Room room101 = new Room();
            room101.setRoomNumber("101");
            room101.setType("Private");
            room101.setWard(westWing);
            roomRepository.save(room101);

            Bed bed1 = new Bed();
            bed1.setBedLabel("Bed 101-A");
            bed1.setOccupied(false);
            bed1.setRoom(room101);
            bedRepository.save(bed1);

            // Create Nurses
            Nurse nurseJackie = new Nurse();
            nurseJackie.setFirstName("Jackie");
            nurseJackie.setLastName("Peyton");
            nurseJackie.setEmail("jackie@hospital.com");
            nurseJackie.setShift("Day Shift");
            nurseJackie.setWard(westWing); // Link to the "West Wing" ward we created earlier
            nurseRepository.save(nurseJackie);

            // 3. Create Doctors
            Doctor drHouse = new Doctor();
            drHouse.setFirstName("Gregory");
            drHouse.setLastName("House");
            drHouse.setEmail("house@hospital.com");
            drHouse.setSpecialization("Diagnostician");
            drHouse.setDepartment(emergency);
            doctorRepository.save(drHouse);

            Doctor drStrange = new Doctor();
            drStrange.setFirstName("Stephen");
            drStrange.setLastName("Strange");
            drStrange.setEmail("strange@hospital.com");
            drStrange.setSpecialization("Neurosurgeon");
            drStrange.setDepartment(cardio);
            doctorRepository.save(drStrange);

            // 4. Create Patients
            Patient patient1 = new Patient();
            patient1.setFirstName("John");
            patient1.setLastName("Doe");
            patient1.setEmail("john.doe@gmail.com");
            patient1.setInsuranceId("INS-9999");
            patientRepository.save(patient1);

            Patient patient2 = new Patient();
            patient2.setFirstName("Jane");
            patient2.setLastName("Smith");
            patient2.setEmail("jane.smith@gmail.com");
            patient2.setInsuranceId("INS-8888");
            patientRepository.save(patient2);

            // 5. Create Appointments
            Appointment app1 = new Appointment();
            app1.setDoctor(drHouse);
            app1.setPatient(patient1);
            app1.setAppointmentTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
            app1.setReason("Severe Leg Pain");
            app1.setStatus("SCHEDULED");
            appointmentRepository.save(app1);

            // 6. Pharmacy (Medicine & Prescriptions)
            Medicine advil = new Medicine();
            advil.setName("Advil");
            advil.setPrice(15.50);
            advil.setStockQuantity(100);
            advil.setManufacturer("Pfizer");
            medicineRepository.save(advil);

            Prescription prescription = new Prescription();
            prescription.setDoctor(drHouse);
            prescription.setPatient(patient1);
            prescription.setIssueDate(LocalDate.now());
            prescription.setNotes("Take twice daily");
            prescriptionRepository.save(prescription);

            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setMedicine(advil);
            item.setQuantity(2);
            item.setDosage("500mg");
            prescriptionItemRepository.save(item);

            // 7. Lab Tests & Results
            LabTest bloodTest = new LabTest();
            bloodTest.setName("Complete Blood Count (CBC)");
            bloodTest.setDescription("Checks overall health and detects disorders");
            bloodTest.setPrice(50.00);
            labTestRepository.save(bloodTest);

            TestResult result1 = new TestResult();
            result1.setPatient(patient1); // John Doe
            result1.setLabTest(bloodTest);
            result1.setTestDate(LocalDateTime.now().minusDays(2));
            result1.setResultNotes("Hemoglobin: 14.5 (Normal). No signs of infection.");
            testResultRepository.save(result1);

            // 8. Invoices
            Invoice inv1 = new Invoice();
            inv1.setPatient(patient1);
            inv1.setTotalAmount(250.00);
            inv1.setIssueDate(LocalDateTime.now().minusDays(1));
            inv1.setPaid(false);
            inv1.setPaymentMethod("Pending Insurance");
            invoiceRepository.save(inv1);

        }
    }
}