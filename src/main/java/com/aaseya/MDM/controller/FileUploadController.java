package com.aaseya.MDM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.aaseya.MDM.service.ConversionService;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final String OUTPUT_DIR = "src/main/java/com/aaseya/MDM/JobWorker/";
    private static final String INPUT_PACKAGE = "com/aaseya/MDM/Java/Delegation/";

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private ConversionService conversionService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.info("File is empty");
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Create the output directory if it doesn't exist
            Path outputDirPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
                logger.info("Created output directory: {}", OUTPUT_DIR);
            }

            // Extract the zip file
            InputStream inputStream = file.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                logger.info("Found entry: {}", entry.getName());

                // Check if the entry is a Java file
                if (!entry.isDirectory() && entry.getName().endsWith(".java")) {
                    logger.info("Processing file: {}", entry.getName());

                    // Get the original file name without the package structure
                    String originalFileName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1); // Extract only the file name
                    Path tempFilePath = outputDirPath.resolve(originalFileName); // Use the original file name for the temp file

                    // Ensure the file is created in the output directory
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFilePath.toFile()))) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }

                    // Convert the Java Delegate file to Job Worker file
                    logger.info("Converting file: {}", tempFilePath.getFileName());
                    conversionService.convertJobDelegateToWorker(tempFilePath, outputDirPath);
                    logger.info("Converted file saved as: {}", tempFilePath.getFileName());
                } else {
                    logger.info("Skipping entry: {} (not a Java file or not in the input package)", entry.getName());
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            return ResponseEntity.ok("Files uploaded and converted successfully.");
        } catch (IOException e) {
            logger.error("Error occurred during file upload: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }}