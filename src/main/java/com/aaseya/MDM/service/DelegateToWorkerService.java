package com.aaseya.MDM.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class DelegateToWorkerService {

	public String convertDelegateToWorker(String filePath, String newClassName) throws Exception {
	    FileInputStream in = new FileInputStream(filePath);
	    
	    // Create an instance of JavaParser
	    JavaParser parser = new JavaParser();
	    
	    // Parse the input stream
	    ParseResult<CompilationUnit> result = parser.parse(in);
	    
	    // Check if the parsing was successful
	    if (result.isSuccessful() && result.getResult().isPresent()) {
	        CompilationUnit cu = result.getResult().get();
	        
	        // Add the import statement for JobWorker
	        cu.addImport("io.camunda.zeebe.spring.client.annotation.JobWorker");

	        // Find the class
	        Optional<ClassOrInterfaceDeclaration> classOpt = cu.getClassByName("JobDelegate");
	        if (classOpt.isEmpty()) throw new RuntimeException("Class not found");

	        ClassOrInterfaceDeclaration clazz = classOpt.get();

	        // Rename the class to the new class name
	        clazz.setName(newClassName);

	        // Remove implements JavaDelegate if present
	        clazz.getImplementedTypes().removeIf(t -> t.getNameAsString().equals("JavaDelegate"));

	        // Add @Component annotation if not present
	        if (!clazz.isAnnotationPresent("Component")) {
	            clazz.addAnnotation(new MarkerAnnotationExpr("Component"));
	        }

	        // Modify method to be JobWorker
	        for (MethodDeclaration method : clazz.getMethods()) {
	            if (method.getNameAsString().equals("execute") || method.getNameAsString().equals("run")) {
	                method.setName("handleJob");
	                method.setType("void");
	                method.getParameters().clear();
	                
	                // Add parameters without 'final'
	                method.addParameter("io.camunda.zeebe.client.api.worker.JobClient", "client");
	                method.addParameter("io.camunda.zeebe.client.api.response.ActivatedJob", "job");

	                method.getAnnotations().clear();
	                method.addAnnotation(new MarkerAnnotationExpr("JobWorker"));

	                // Create a block statement for the method body
	                String methodBody = "{\n" +
	                        "    System.out.println(\"Handling job: \" + job.getVariables());\n" +
	                        "    client.newCompleteCommand(job.getKey())\n" +
	                        "          .variables(\"{\\\"greeting\\\": \\\"Hello from worker!\\\"}\")\n" +
	                        "          .send();\n" +
	                        "}";
	                
	                // Parse the block statement
	                ParseResult<BlockStmt> blockResult = parser.parseBlock(methodBody);
	                
	                // Check if the parsing was successful
	                if (blockResult.isSuccessful() && blockResult.getResult().isPresent()) {
	                    method.setBody(blockResult.getResult().get());
	                } else {
	                    throw new RuntimeException("Failed to parse the method body.");
	                }
	            }
	        }

	        // Save the modified class to a new file
	        String outputPath = Paths.get("src/main/java/com/example/demo/ZeebeWorker/" + newClassName + ".java").toString();
	        FileWriter writer = new FileWriter(outputPath);
	        writer.write(cu.toString());
	        writer.close();

	        return "Job worker class generated at: " + outputPath;
	    } else {
	        throw new RuntimeException("Failed to parse the Java file.");
	    }
	}
	
	
	
	 public String convertDelegatesInPackage(String packagePath) throws Exception {
	        File packageDir = new File(packagePath);
	        if (!packageDir.exists() || !packageDir.isDirectory()) {
	            throw new RuntimeException("Invalid package path: " + packagePath);
	        }

	        StringBuilder result = new StringBuilder();
	        File[] javaFiles = packageDir.listFiles((dir, name) -> name.endsWith(".java"));
	        if (javaFiles != null) {
	            for (File javaFile : javaFiles) {
	                String convertedFilePath = convertDelegateToWorker(javaFile);
	                result.append("Converted: ").append(convertedFilePath).append("\n");
	            }
	        }
	        return result.toString();
	    }

	 public String convertDelegateToWorker(File javaFile) throws Exception {
		    FileInputStream in = new FileInputStream(javaFile);
		    JavaParser parser = new JavaParser();
		    ParseResult<CompilationUnit> result = parser.parse(in);

		    if (result.isSuccessful() && result.getResult().isPresent()) {
		        CompilationUnit cu = result.getResult().get();

		        Optional<ClassOrInterfaceDeclaration> classOpt = cu.getPrimaryType()
		            .filter(t -> t instanceof ClassOrInterfaceDeclaration)
		            .map(t -> (ClassOrInterfaceDeclaration) t);

		        if (classOpt.isEmpty()) {
		            throw new RuntimeException("No class found in file: " + javaFile.getName());
		        }

		        ClassOrInterfaceDeclaration clazz = classOpt.get();
		        String originalClassName = clazz.getNameAsString();
		        String newClassName = originalClassName.replace("Delegate", "") + "JobWorker";

		        // Add imports
		        cu.addImport("io.camunda.zeebe.spring.client.annotation.JobWorker");
		        cu.addImport("io.camunda.zeebe.client.api.worker.JobClient");
		        cu.addImport("io.camunda.zeebe.client.api.response.ActivatedJob");
		        cu.addImport("org.springframework.stereotype.Component");

		        clazz.setName(newClassName);
		        clazz.getImplementedTypes().removeIf(t -> t.getNameAsString().equals("JavaDelegate"));

		        if (!clazz.isAnnotationPresent("Component")) {
		            clazz.addAnnotation(new MarkerAnnotationExpr("Component"));
		        }

		        for (MethodDeclaration method : clazz.getMethods()) {
		            if (method.getNameAsString().equals("execute")) {
		                method.setName("handleJob");
		                method.setType("void");
		                method.getParameters().clear();
		                method.addParameter("JobClient", "client");
		                method.addParameter("ActivatedJob", "job");
		                method.getAnnotations().clear();
		                method.addAnnotation(new MarkerAnnotationExpr("JobWorker"));

		                String methodBody = "{\n" +
		                        "    System.out.println(\"Handling job: \" + job.getVariables());\n" +
		                        "    client.newCompleteCommand(job.getKey())\n" +
		                        "          .variables(job.getVariables())\n" +
		                        "          .send();\n" +
		                        "}";

		                ParseResult<BlockStmt> blockResult = parser.parseBlock(methodBody);
		                if (blockResult.isSuccessful() && blockResult.getResult().isPresent()) {
		                    method.setBody(blockResult.getResult().get());
		                } else {
		                    throw new RuntimeException("Failed to parse the method body.");
		                }
		            }
		        }

		        String outputPath = Paths.get("src/main/java/com/aaseya/MDM/jobworker/" + newClassName + ".java").toString();
		        FileWriter writer = new FileWriter(outputPath);
		        writer.write(cu.toString());
		        writer.close();

		        return outputPath;
		    } else {
		        throw new RuntimeException("Failed to parse the Java file: " + javaFile.getName());
		    }
		}
	 
	 
	 public String convertAllDelegates(String sourcePackagePath, String targetPackagePath, String basePackage) throws Exception {
	        File folder = new File(sourcePackagePath);
	        if (!folder.exists() || !folder.isDirectory()) {
	            throw new IllegalArgumentException("Invalid source package path");
	        }

	        File[] files = folder.listFiles((dir, name) -> name.endsWith(".java"));
	        if (files == null || files.length == 0) {
	            throw new IllegalStateException("No Java files found in the source package");
	        }

	        StringBuilder summary = new StringBuilder();

	        for (File file : files) {
	            String originalName = file.getName().replace(".java", "");
	            String newName = originalName.replace("Delegate", "") + "JobWorker";

	            convertSingleDelegate(file.getPath(), targetPackagePath, basePackage, newName);
	            summary.append("Converted: ").append(originalName).append(" → ").append(newName).append("\n");
	        }

	        return summary.toString();
	    }

	    private void convertSingleDelegate(String filePath, String targetPackagePath, String basePackage, String newClassName) throws Exception {
	        FileInputStream in = new FileInputStream(filePath);
	        JavaParser parser = new JavaParser();
	        ParseResult<CompilationUnit> result = parser.parse(in);

	        if (result.isSuccessful() && result.getResult().isPresent()) {
	            CompilationUnit cu = result.getResult().get();

	            // Set new package
	            cu.setPackageDeclaration(basePackage);

	            // Add necessary imports
	            cu.addImport("io.camunda.zeebe.spring.client.annotation.JobWorker");
	            cu.addImport("io.camunda.zeebe.client.api.response.ActivatedJob");
	            cu.addImport("io.camunda.zeebe.client.api.worker.JobClient");

	            // Get the class
	            Optional<ClassOrInterfaceDeclaration> clazzOpt = cu.getClassByName(new File(filePath).getName().replace(".java", ""));
	            if (clazzOpt.isEmpty()) {
	                throw new RuntimeException("No class found in file: " + filePath);
	            }

	            ClassOrInterfaceDeclaration clazz = clazzOpt.get();
	            clazz.setName(newClassName);
	            clazz.getImplementedTypes().removeIf(t -> t.getNameAsString().equals("JavaDelegate"));
	            clazz.getMethodsByName("execute").forEach(method -> {
	                method.setName("handleJob");
	                method.setType("void");
	                method.getParameters().clear();
	                method.addParameter("JobClient", "client");
	                method.addParameter("ActivatedJob", "job");
	                method.getAnnotations().clear();
	                method.addAnnotation(new MarkerAnnotationExpr("JobWorker"));

	                // You can further enhance this to convert execution variable reads/writes to job.getVariablesAsMap()
	            });

	            // Ensure @Component annotation is present
	            if (!clazz.isAnnotationPresent("Component")) {
	                clazz.addAnnotation(new MarkerAnnotationExpr("Component"));
	            }

	            // Write to target path
	            File targetDir = new File(targetPackagePath);
	            if (!targetDir.exists()) {
	                targetDir.mkdirs();
	            }

	            File outputFile = new File(targetDir, newClassName + ".java");
	            FileWriter writer = new FileWriter(outputFile);
	            writer.write(cu.toString());
	            writer.close();
	        } else {
	            throw new RuntimeException("Failed to parse: " + filePath);
	        }
	    }

}
