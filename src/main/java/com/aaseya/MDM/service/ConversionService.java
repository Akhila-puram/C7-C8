package com.aaseya.MDM.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConversionService {

    private static final Logger logger = LoggerFactory.getLogger(ConversionService.class);

    public void convertJobDelegateToWorker(Path filePath, Path outputDir) throws IOException {
        logger.info("Reading content from: {}", filePath);

        List<String> lines = Files.readAllLines(filePath);
        String className = filePath.getFileName().toString().replace(".java", "");
        String jobType = className.substring(0, 1).toLowerCase() + className.substring(1);

        // Retain necessary imports except Camunda 7-specific ones
        List<String> retainedImports = lines.stream()
                .filter(line -> line.startsWith("import ")
                        && !line.contains("org.camunda.bpm.engine.delegate"))
                .collect(Collectors.toList());

        // Remove package and Camunda 7 imports, get class content
        List<String> body = lines.stream()
                .filter(line -> !line.startsWith("import ")
                        && !line.startsWith("package "))
                .collect(Collectors.toList());

        String updatedBody = String.join("\n", body);

        // Remove JavaDelegate implementation and override annotation
        updatedBody = updatedBody
                .replaceAll("implements\\s+JavaDelegate", "")
                .replaceAll("@Override\\s*", "")
                .replaceAll("(?m)^\\s*@Component\\s*", ""); // Remove any existing @Component

        boolean hasVariables = updatedBody.contains("execution.setVariable(") || updatedBody.contains("execution.getVariable(");

        // Replace method signature with handleJob — now handles presence or absence of `throws`
        updatedBody = updatedBody.replaceAll(
                "public\\s+void\\s+execute\\s*\\(.*?\\)\\s*(throws\\s+\\w+(\\s*,\\s*\\w+)*)?\\s*\\{",
                "@JobWorker(type = \"" + jobType + "\")\n"
                        + "    public void handleJob(final JobClient client, final ActivatedJob job) throws Exception {\n"
                        + (hasVariables ? "        Map<String, Object> variables = job.getVariablesAsMap();\n" : "")
        );

        // Replace setVariable and getVariable usage
        updatedBody = updatedBody.replaceAll("execution\\.setVariable\\(", "variables.put(");
        updatedBody = updatedBody.replaceAll("execution\\.getVariable\\(", "variables.get(");

        // Remove any references to execution
        updatedBody = updatedBody.replaceAll("execution\\.", "");

        // Inject job completion logic
        updatedBody = updatedBody.replaceAll(
                "(?s)(public void handleJob.*?\\{)(.*?)(\\n\\s*})",
                "$1$2\n\n        try {\n"
                        + "            client.newCompleteCommand(job.getKey())\n"
                        + "                  .variables(" + (hasVariables ? "variables" : "Map.of()") + ")\n"
                        + "                  .send()\n"
                        + "                  .join();\n"
                        + "        } catch (Exception e) {\n"
                        + "            client.newFailCommand(job.getKey())\n"
                        + "                  .retries(0)\n"
                        + "                  .send()\n"
                        + "                  .join();\n"
                        + "            throw e;\n"
                        + "        }\n    }"
        );

        // Start building final output
        StringBuilder finalContent = new StringBuilder();
        finalContent.append("package com.aaseya.MDM.JobWorker;\n\n");

        // Add required Zeebe and Spring imports
        finalContent.append("import io.camunda.zeebe.client.api.worker.JobClient;\n")
                .append("import io.camunda.zeebe.client.api.response.ActivatedJob;\n")
                .append("import io.camunda.zeebe.spring.client.annotation.JobWorker;\n")
                .append("import org.springframework.stereotype.Component;\n")
                .append("import java.util.Map;\n")
                .append("import java.util.HashMap;\n");

        // Add any retained original imports
        for (String imp : retainedImports) {
            finalContent.append(imp).append("\n");
        }

        // Add Autowired import if needed
        if (updatedBody.contains("@Autowired")) {
            finalContent.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        }

        // Finalize with @Component + updated class content
        finalContent.append("\n@Component\n")
                .append(updatedBody.trim());

        // Write output file
        Path newFilePath = outputDir.resolve(className + ".java");
        Files.writeString(newFilePath, finalContent.toString());

        logger.info("Converted file saved to: {}", newFilePath);
    }
}
