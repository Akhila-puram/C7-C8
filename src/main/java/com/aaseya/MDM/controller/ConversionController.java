package com.aaseya.MDM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.aaseya.MDM.service.ConversionService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/conversion")
public class ConversionController {

    @Autowired
    private ConversionService conversionService;

    @PostMapping("/convert")
    public ResponseEntity<String> convertJobDelegate(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a zip file.");
        }

        try {
            // Extract the zip file
            Path tempDir = Files.createTempDirectory("extracted");
            unzip(file.getInputStream(), tempDir);

            // Convert the files
            convertFiles(tempDir);

            // Clean up the temporary files
            Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a)) // reverse order to delete files
                .map(Path::toFile)
                .forEach(fileToDelete -> fileToDelete.delete());
            tempDir.toFile().delete();

            return ResponseEntity.ok("Conversion successful!");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
        }
    }

    private void unzip(InputStream zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    // Ensure parent directories are created
                    Files.createDirectories(newPath.getParent());
                    // Write the file
                    Files.copy(zis, newPath);
                }
                zis.closeEntry();
            }
        }
    }

    private void convertFiles(Path directory) {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".java")) // Process only Java files
                 .forEach(path -> {
                     try {
                         // Pass the output directory to the conversion method
                         Path outputDir = Path.of("src/main/java/com/aaseya/MDM/JobWorker/");
                         conversionService.convertJobDelegateToWorker(path, outputDir);
                     } catch (IOException e) {
                         System.err.println("Error converting file: " + path + " - " + e.getMessage());
                     }
                 });
        } catch (IOException e) {
            System.err.println("Error reading directory: " + directory + " - " + e.getMessage());
        }
    }
}