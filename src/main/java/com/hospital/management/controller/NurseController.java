package com.hospital.management.controller;

import com.hospital.management.model.Nurse;
import com.hospital.management.repository.NurseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nurses")
@CrossOrigin(origins = "*")
public class NurseController {

    private final NurseRepository nurseRepository;

    public NurseController(NurseRepository nurseRepository) {
        this.nurseRepository = nurseRepository;
    }

    @GetMapping
    public List<Nurse> getAllNurses() {
        return nurseRepository.findAll();
    }

    @PostMapping
    public Nurse addNurse(@RequestBody Nurse nurse) {
        return nurseRepository.save(nurse);
    }
}