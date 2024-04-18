//FileUploadController.java

package com.fileParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
public class FileUploadController {
	
	private WordReaderService wordReaderService;
	
	public String wordDoc;
	
	public void insertDataIntoString(String data) {
        // Insert data into the string variable
        wordDoc = data;
    }
	
	@Autowired
	public FileUploadController(WordReaderService wordReaderService) {
		this.wordReaderService = wordReaderService;
	}
	
	
	@PostMapping("/upload-file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
        	
        	
            // Save the uploaded file to a temporary directory on the server
            File tempFile = File.createTempFile("Uploaded file: ", null);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            outputStream.write(file.getBytes());
            outputStream.close();

            // Example processing: Log file name
            System.out.println("Uploaded file: " + tempFile.getName());

            String wordDoc1 = wordReaderService.readWordDoc(file);
            insertDataIntoString(wordReaderService.readWordDoc(file));
//            System.out.println("Extracted to string: ");
//            System.out.println(wordDoc1);
            insertDataIntoString(wordDoc1);
            //Extract and print the contents of uploaded file
//            extractedDocContents(tempFile);
            
            // Return a response indicating successful upload
            return ResponseEntity.ok("File uploaded successfully: " + tempFile.getName());
        } catch (IOException e) {
            // Handle any exceptions that occur during file upload
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to upload file: " + e.getMessage());
        }
    }
	
//	private void extractedDocContents(File docFile) throws IOException, TikaE
	
	@GetMapping("/questions")
	public ResponseEntity<Map<String, String>> parseQuestions(){
		Map<String, String> parsedData = wordReaderService.parseQuestionsWithOptions(wordDoc);
		for (Map.Entry<String, String> entry : parsedData.entrySet()) {
		    String question = entry.getKey();
		    String options = entry.getValue();
		    
//		    System.out.println("Question: " + question);
//		    System.out.println("Options:");
//		    System.out.println(options);
//		    System.out.println(); // Add an empty line between questions for better readability
		}
		System.out.println("headerText: ");
		System.out.println(wordReaderService.headerText);
		System.out.println("wordDoc: ");
	    System.out.println(wordDoc);
	    System.out.println(wordReaderService.extractLessonNameFromHeader(wordReaderService.headerText));
		return new ResponseEntity<>(parsedData, HttpStatus.OK);
	}
	
	
	@GetMapping("/unparsedData")
	public String getAllQuestions() {
		return wordDoc;
	}
}
