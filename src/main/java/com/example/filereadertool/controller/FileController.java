package com.example.filereadertool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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


    @PostMapping("/upload/repo")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        String UPLOAD_REPO_DIR = "C:/Users/Ced/Documents/Springboot/FileReader/repository";
        String originalName = file.getOriginalFilename();
        try {
            File dest = new File(UPLOAD_REPO_DIR + "/" + originalName);
            if (isValidFileExtension(originalName)) {
                file.transferTo(dest);
                model.addAttribute("repoSuccess", "Upload Successful \n" + originalName);
                return "/static/uploadPage";
            } else {
                model.addAttribute("repoError", "Invalid file extension for file: " + originalName + ". Supported extensions: .docx, .csv, .txt, .sql");
                return "/static/uploadPage";
            }
        } catch (IOException e) {
//            e.printStackTrace();
            // Handle the error appropriately.
            model.addAttribute("repoError", "Invalid file extension for file: " + originalName + ". Supported extensions: .docx, .csv, .txt, .sql");
            return "/static/uploadPage";
        }
//        return "redirect:/file/uploadPage";
    }

    @PostMapping("/process")
    public String process() {
        final File folder = new File("C:/Users/Ced/Documents/Springboot/FileReader/input");
        listFilesForFolder(folder);
        return "redirect:/success";
    }

    // Read All Files in a Folder
    public static void listFilesForFolder(final File folder) {

        String fileName = "";


        // Declare global variable where we store All WhiteSpaces and found words
        int allWhitespaces = 0;


        // START -- for loop to read each file inside the folder
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileName = fileEntry.getName();
                // Print File Name (eg. Book1.csv)
                System.out.println(fileName);

                // Read the content of each file in the folder
                Path file = Paths.get("C:/Users/Ced/Documents/Springboot/FileReader/input/"+fileName);
                // START -- TRY CATCH
                try {
                    // Declare Repository Folder
                    final File repoFolder = new File("C:/Users/Ced/Documents/Springboot/FileReader/repository");

                    // Store content of each file in a List<String>
                    List<String> linesOfEachFile = Files.readAllLines(file, StandardCharsets.UTF_8);
                    List<String> foundWords = new ArrayList<String>();

                    // Print content of each file (Testing in Console)
                    // Compare strToCompare == str in repository
                    for (String strToSearch : linesOfEachFile) {
                        System.out.println(strToSearch);


                        if(!strToSearch.isEmpty()){
//                              System.out.println(strToSearch);
                            File dir = new File("C:/Users/Ced/Documents/Springboot/FileReader/repository");
                            // Call the method for searching/comparing if file contains strToSearch variable and store it in List<String> theWords
                            List<String> theWords = searchForWord(strToSearch);
                            // Print Found words in console
                            for (String words : theWords) {
                                System.out.println("********* " + words);
                                foundWords.add(words);
                            }

                        }
                    }

                    // Count number of lines/rows
                    int numberOfLines = linesOfEachFile.size();
                    System.out.println("Number of Lines: " + numberOfLines);

                    // Count number of empty rows
                    // Declare variable where we store Empty Rows
                    long emptyRows = linesOfEachFile.stream()
                            .filter(x->x.isEmpty())
                            .count();
                    System.out.println("Empty Rows: " + emptyRows);


                    // Declare variable where we store Whitespace/s
                    int whitespace = 0;
                    // Count whitespaces
                    for (String str: linesOfEachFile) {
                        long spaces = str.chars().filter(c -> c == (int)' ').count();
                        whitespace += spaces;
                    }

                    // Print row whitespaces and total number of whitespaces
                    System.out.println("Whitespace(s): " + whitespace);
                    allWhitespaces = (int) (emptyRows + whitespace);
                    System.out.println("All Whitespaces: " + allWhitespaces);

                    // Call the Write Method
                    // Variables to pass to the Write Method
                    // fileName, numberOfLines, emptyRows, whitespace, allWhitespaces
                    String[] requirements = {"Number of Lines: "+ numberOfLines, "Empty Rows: "+(int) emptyRows};
                    // Call write method
                    write(fileName, requirements, foundWords)
                    ;
                    // Reset
                    whitespace = 0;
                    allWhitespaces = 0;
//                          size = 0;
//                          theWord.clear();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // END -- TRY CATCH

                System.out.println("--------------------------------");
                // CODE HERE THE WRITE/OUTPUT FILE
                // Variable needed allWhitespaces, whitespace, emptyRows, searchFound, List<String> searched words
            }
        } // END -- for loop to read each file inside the folder

        // Code here after the For Loop

    } // End of method


    public static void write(String fileName, String[] requirements, List<String> foundWords) throws IOException{

        String theFileName = "";

        if(fileName.contains(".csv")) {
            theFileName = fileName.replaceAll(".csv", ".log");
        } else if (fileName.contains(".txt")) {
            theFileName = fileName.replaceAll(".txt", ".log");
        } else if (fileName.contains(".sql")) {
            theFileName = fileName.replaceAll(".sql", ".log");
        }

        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter("C:/Users/Ced/Documents/Springboot/FileReader/output/" + theFileName ));

        String[] found = new String[foundWords.size()];
        for(int i = 0; i < foundWords.size(); i++) found[i] = foundWords.get(i);

        for (int i = 0; i < requirements.length; i++) {

            // Maybe:
//          outputWriter.write(requirements[i]+ "\n\n" + found[i] );
            outputWriter.write(requirements[i]+ "");
            // Or:
            // outputWriter.write(Integer.toString(requirements[i]));
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();

        for (String word : foundWords ){
            System.out.println("*********************" +word);
        }
//
        File log = new File("C:/Users/Ced/Documents/Springboot/FileReader/output/" + theFileName );

        try{
            if(!log.exists()){
                System.out.println("We had to make a new file.");
                log.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(log, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(Arrays.deepToString(found).replace(",", "\n"));
            bufferedWriter.close();

            System.out.println("Done");
        } catch(IOException e) {
            System.out.println("COULD NOT LOG!!");
        }
    }

    private static List<String> searchForWord(String strToSearch) throws IOException {
        String file = "C:/Users/Ced/Documents/Springboot/FileReader/repository";

        List<String> theWords = new ArrayList<String>();

        File dir = new File(file);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
                String fileName = child.getName();
//            System.out.println("I found: " + fileName);

                Path readAllLines = Paths.get("C:/Users/Ced/Documents/Springboot/FileReader/repository/"+fileName);

                List<String> lines = Files.readAllLines(readAllLines, StandardCharsets.UTF_8);
//            List<String> lines = Files.readAllLines(Paths.get(fileName));
                int getRowNumber = 0;
                for (String line : lines) {
                    getRowNumber++;
                    if (line.contains(strToSearch)) {
                        String theWord = "Found: " + "\"" + strToSearch + "\"" + " on row: #" + getRowNumber;
//                      System.out.println("Found: " + "\"" + strToSearch + "\"" + " in file: " + fileName + " on row: #" + getRowNumber);
                        // Store found words in a List<String> and pass it to the calling method
                        theWords.add(theWord);
                    }
                } getRowNumber = 0;

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        return theWords;

    }


}
