package com.university.healthcenter.service;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;



public interface PDFService {
    String extractTextFromPDF(MultipartFile file) throws IOException;
}
