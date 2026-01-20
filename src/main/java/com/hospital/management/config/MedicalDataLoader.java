package com.hospital.management.config;

import com.hospital.management.model.*;
import com.hospital.management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class MedicalDataLoader {

    @Bean
    CommandLineRunner initMedicalData(
            MedicineRepository medicineRepo,
            MedicalRecordRepository recordRepo,
            PrescriptionRepository prescriptionRepo,
            PrescriptionItemRepository itemRepo,
            PatientRepository patientRepo,
            DoctorRepository doctorRepo
    ) {
        return args -> {
            List<Patient> patients = patientRepo.findAll();
            List<Doctor> doctors = doctorRepo.findAll();

            if (patients.isEmpty() || doctors.isEmpty()) {
                System.out.println("⚠️ Skipping Medical Data Load: No patients or doctors found.");
                return;
            }

            Patient patient = patients.get(0);
            Doctor doctor = doctors.get(0);

            // 1. Check Medicines - Ensure we have at least 2!
            List<Medicine> meds = medicineRepo.findAll();

            if (meds.size() < 2) {
                System.out.println("💊 Creating missing medicines...");
                Medicine m1 = new Medicine();
                m1.setName("Amoxicillin");
                m1.setDescription("Antibiotic");
                m1.setPrice(15.0);
                m1.setStockQuantity(100);
                m1.setManufacturer("Pfizer");

                Medicine m2 = new Medicine();
                m2.setName("Ibuprofen");
                m2.setDescription("Painkiller");
                m2.setPrice(8.50);
                m2.setStockQuantity(200);
                m2.setManufacturer("Advil");

                medicineRepo.saveAll(List.of(m1, m2));

                // REFRESH THE LIST after saving
                meds = medicineRepo.findAll();
            }

            // 2. Create a Medical History Record
            if (recordRepo.count() == 0) {
                MedicalRecord record = new MedicalRecord();
                record.setPatient(patient);
                record.setDoctor(doctor);
                record.setDateOfVisit(LocalDate.now().minusDays(5).atStartOfDay());
                record.setDiagnosis("Acute Bronchitis");
                record.setTreatmentPlan("Rest, hydration, and antibiotics. Follow up in 7 days.");
                recordRepo.save(record);
            }

            // 3. Create a Prescription
            if (prescriptionRepo.count() == 0) {
                Prescription rx = new Prescription();
                rx.setPatient(patient);
                rx.setDoctor(doctor);
                rx.setIssueDate(LocalDate.now());
                rx.setNotes("Take with food.");

                prescriptionRepo.save(rx);

                // Add Items
                if (meds.size() >= 2) {
                    PrescriptionItem item1 = new PrescriptionItem();
                    item1.setPrescription(rx);
                    item1.setMedicine(meds.get(0));
                    item1.setQuantity(1);
                    item1.setDosage("500mg every 8 hours");

                    PrescriptionItem item2 = new PrescriptionItem();
                    item2.setPrescription(rx);
                    item2.setMedicine(meds.get(1));
                    item2.setQuantity(1);
                    item2.setDosage("400mg as needed for pain");

                    itemRepo.saveAll(List.of(item1, item2));
                }
            }

            System.out.println("✅ Medical Records and Prescriptions Loaded!");
        };
    }
}