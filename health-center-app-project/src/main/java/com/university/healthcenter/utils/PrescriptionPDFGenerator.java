package com.university.healthcenter.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrescriptionPDFGenerator {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionPDFGenerator.class);
    
    public static void generateSamplePrescription(String patientName, String[] medications, String outputPath) {
        try {
            // Create output directory if it doesn't exist
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Header
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText("University Health Center");
                    contentStream.endText();

                    // Date
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(50, 720);
                    contentStream.showText("Date: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    contentStream.endText();

                    // Patient Information
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(50, 690);
                    contentStream.showText("Patient Name: " + patientName);
                    contentStream.endText();

                    // Prescription Header
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.newLineAtOffset(50, 650);
                    contentStream.showText("Prescription");
                    contentStream.endText();

                    // Medications
                    float yPosition = 620;
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    for (String medication : medications) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(70, yPosition);
                        contentStream.showText("Medicine: " + medication);
                        yPosition -= 20;
                        // Add dosage information
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(90, yPosition);
                        contentStream.showText("Take 3 times daily after meals");
                        contentStream.endText();
                        yPosition -= 40;
                    }

                    // Doctor's Signature
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(50, 200);
                    contentStream.showText("Dr. Smith");
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(50, 180);
                    contentStream.showText("University Health Center");
                    contentStream.endText();
                }

                document.save(outputFile);
                logger.info("Generated prescription PDF: {}", outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error generating prescription PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate prescription PDF", e);
        }
    }

    public static void generateSamplePrescriptions() {
        // Create a samples directory in the project root
        String baseDir = "samples/prescriptions";
        
        // Sample 1
        generateSamplePrescription(
            "John Doe",
            new String[]{"Amoxicillin 500mg", "Ibuprofen 400mg"},
            baseDir + "/prescription_sample1.pdf"
        );

        // Sample 2
        generateSamplePrescription(
            "Jane Smith",
            new String[]{"Paracetamol 500mg", "Cetirizine 10mg", "Vitamin C 1000mg"},
            baseDir + "/prescription_sample2.pdf"
        );

        // Sample 3
        generateSamplePrescription(
            "Mike Johnson",
            new String[]{"Omeprazole 20mg", "Multivitamin Complex"},
            baseDir + "/prescription_sample3.pdf"
        );
    }

    public static void main(String[] args) {
        logger.info("Starting to generate sample prescriptions...");
        generateSamplePrescriptions();
        logger.info("Finished generating sample prescriptions!");
    }
}