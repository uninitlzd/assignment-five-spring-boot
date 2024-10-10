package com.alfredoeka.spring_assignment_five.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/karyawan")
public class KaryawanController {

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getKaryawanData() {
        return ResponseEntity.ok("Karyawan data");
    }
}
