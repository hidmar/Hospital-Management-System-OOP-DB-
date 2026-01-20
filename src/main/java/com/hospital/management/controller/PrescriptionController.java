package com.hospital.management.controller;

import com.hospital.management.model.Prescription;
import com.hospital.management.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository repository;

    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return repository.findAll();
    }

    @PostMapping
    public Prescription createPrescription(@RequestBody Prescription prescription) {
        // Critical: Link items to the prescription before saving
        if (prescription.getItems() != null) {
            prescription.getItems().forEach(item -> item.setPrescription(prescription));
        }
        return repository.save(prescription);
    }
}