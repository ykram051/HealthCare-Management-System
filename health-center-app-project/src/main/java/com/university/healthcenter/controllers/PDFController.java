package com.university.healthcenter.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.university.healthcenter.service.PDFService;

@RestController
@RequestMapping("/api/pdf")
public class PDFController {

    private final PDFService pdfService;

    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extractTextFromPDF(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = pdfService.extractTextFromPDF(file);
            return ResponseEntity.ok(extractedText);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to extract text: " + e.getMessage());
        }
    }
}