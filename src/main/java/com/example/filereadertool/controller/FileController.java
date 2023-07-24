package com.example.filereadertool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    private static final String UPLOAD_DIR = "C:/Users/Ced/Documents/Springboot/FileReader/input";

    @GetMapping("/uploadPage")
    public String showUploadPage() {
        return "static/uploadPage";
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, Model model) {
        List<String> successFiles = new ArrayList<>();
        List<String> errorFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errorFiles.add("File upload failed. Please make sure you have selected a file.");
                continue;
            }

            try {
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                String fileExtension = getFileExtension(originalFilename);

                if (!isValidFileExtension(fileExtension)) {
                    errorFiles.add("Invalid file extension for file: " + originalFilename + ". Supported extensions: .docx, .csv, .txt, .sql");
                    continue;
                }

                Path uploadPath = Paths.get(UPLOAD_DIR, originalFilename);
                Files.copy(file.getInputStream(), uploadPath);
                successFiles.add(originalFilename);

            } catch (IOException e) {
                errorFiles.add("Failed to upload file: " + file.getOriginalFilename());
            }
        }

        if (!successFiles.isEmpty()) {
            model.addAttribute("success", "Upload Successful for files: \n" + String.join(", ", successFiles));
        }
        if (!errorFiles.isEmpty()) {
            model.addAttribute("error", String.join("<br>", errorFiles));
        }

        return "static/uploadPage";
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isValidFileExtension(String extension) {
        return extension.equals("docx") || extension.equals("csv") || extension.equals("txt") || extension.equals("sql");
    }

//    public ResponseEntity<FileErrorResponse> handleException(FileNotFound exc) {
//
//        FileErrorResponse error = new FileErrorResponse();
//
//        error.setStatus(HttpStatus.NOT_FOUND.value());
//        error.setMessage(exc.getMessage());
//        error.setTimeStamp(System.currentTimeMillis());
//
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }

//    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
//        if (file.isEmpty()) {
////            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
//            model.addAttribute("error", "");
//            return "/static/uploadPage";
//        }
//
//        try {
//            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
//            String fileExtension = getFileExtension(originalFilename);
//
//            if (!isValidFileExtension(fileExtension)) {
////                return new ResponseEntity<>("Invalid file extension. Supported extensions: .docx, .csv, .txt, .sql", HttpStatus.BAD_REQUEST);
//                model.addAttribute("error", "");
//                return "/static/uploadPage";
//            }
//
//            Path uploadPath = Paths.get(UPLOAD_DIR, originalFilename);
//            Files.copy(file.getInputStream(), uploadPath);
//
////            return new ResponseEntity<>("File uploaded successfully.", HttpStatus.OK);
//            model.addAttribute("success", "Upload Successful \n" + originalFilename);
//            return "/static/uploadPage";
//        } catch (IOException e) {
////            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
//            model.addAttribute("error", "");
//            return "/static/uploadPage";
//        }
//    }

//    private String getFileExtension(String fileName) {
//        int dotIndex = fileName.lastIndexOf('.');
//        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
//    }
//
//    private boolean isValidFileExtension(String extension) {
//        return extension.equals("docx") || extension.equals("csv") || extension.equals("txt") || extension.equals("sql");
//    }



//    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
//        try {
//            String originalName = file.getOriginalFilename();
//            File dest = new File(UPLOAD_DIR + "/" + originalName);
//            file.transferTo(dest);
//            model.addAttribute("success", "Upload Successful \n" + originalName);
//            return "/static/uploadPage";
//        } catch (IOException e) {
////            e.printStackTrace();
//            // Handle the error appropriately.
//            model.addAttribute("error", "");
//            return "/static/uploadPage";
//        }
////        return "redirect:/file/uploadPage";
//    }


//    public ResponseEntity<FileErrorResponse> handleException(FileNotFound exc) {
//
//        FileErrorResponse error = new FileErrorResponse();
//
//        error.setStatus(HttpStatus.NOT_FOUND.value());
//        error.setMessage(exc.getMessage());
//        error.setTimeStamp(System.currentTimeMillis());
//
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
}
