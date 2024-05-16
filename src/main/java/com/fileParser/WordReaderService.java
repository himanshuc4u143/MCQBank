//WOrdReaderService.java

package com.fileParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WordReaderService {
	
	public String headerText;
	
	private final WordReaderRepo wordReaderRepo;
	
	@Autowired
	public WordReaderService(WordReaderRepo wordReaderRepo) {
		this.wordReaderRepo = wordReaderRepo;
	}
	
	public void insertDataIntoString(String data) {
        // Insert data into the string variable
        headerText = data;
    }
	
	private List<Question> ques = new ArrayList<Question>();
	
	
	//{--> Main Methods <<--}
	public List<Question> getAllQuestions() {
		System.out.println("inside getAllQuestions service");
        return ques;
    }
    
    public void addQuestions(List<Question> newQues) {
        ques.addAll(newQues);
    }
    
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //{--> Supporting Methods <<--}
	public String readWordDoc(MultipartFile file) throws IOException{
		// TODO Auto-generated method stub
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try(InputStream fis = file.getInputStream();
		XWPFDocument doc = new XWPFDocument(fis)){
		
			//Read header text
			for (XWPFHeader header : doc.getHeaderList()) {
				List<XWPFParagraph> paragraphs = header.getParagraphs();
				
				for (XWPFParagraph paragraph : paragraphs) {
					//System.out.println("Header: "+paragraph.getText());
					stringBuilder.append(paragraph.getText());
					stringBuilder.append("\n");
				}
			}
			
			//Header Data sent Instantly to header Parse methods through headerText
			insertDataIntoString(stringBuilder.toString()); 
			
			//Iterate through paras
			for(XWPFParagraph paragraph :  doc.getParagraphs()) {
				//System.out.println(paragraph.getText());
				stringBuilder.append(paragraph.getText());
				stringBuilder.append("\n");
			}
		
			//parseQuestionsWithOptions(stringBuilder.toString());
		}
//		System.out.println("Inside readWordDoc");
//		System.out.println();
//		System.out.println();
//		System.out.println("sdnjfv");
//		System.out.println();
		
		String rawText = stringBuilder.toString();
		
		//routed to parseQuestionsWithOptionsList method for parsing the raw text into que n options
		parseQuestionsWithOptionsList(rawText);
		return rawText;
	}
	
	public Map<String, String> parseQuestionsWithOptionsMap(String text){
		Map<String, String> questionsWithOptions = new HashMap<String, String>();
		String[] lines = text.split("\n");
		String currentQuestion = "";
		String currentOptions = "";
//		System.out.println("text: " +text);
//		System.out.println();
//		System.out.println();

		for(String line: lines) {
			if(line.matches("^\\d+.(.*)")) { // Line starts with a number followed by a dot
				
				// Save the previous question and options
				if(!currentQuestion.isEmpty() && !currentOptions.isEmpty()) {
					questionsWithOptions.put(currentQuestion, currentOptions);
					
				}
				
				//Extract new Que
				currentQuestion = line.substring(line.indexOf('.') + 1).trim();
//				System.out.println("Current Que: "+currentQuestion);
			} 
			else if (line.matches("^[a-d]\\.\\s*.*")) { // Line starts with lowercase letter followed by .
				
				//Extract option
				currentOptions = line.substring(line.indexOf('.') - 1).trim();
//				System.out.println("Current Options: "+currentOptions);
			}
		}
		
		//add the last que and options
		if(!currentQuestion.isEmpty() && !currentOptions.isEmpty()) {
			questionsWithOptions.put(currentQuestion, currentOptions);
		}
		//Print output for testing
		/*for (Map.Entry<String, String> entry : questionsWithOptions.entrySet()) {
		    String question = entry.getKey();
		    String options = entry.getValue();
		    
		    System.out.println("Question: " + question);
		    System.out.println("Options:");
		        System.out.println(options);
		    
		    System.out.println(); // Add an empty line between questions for better readability
		}*/
		
//		System.out.println();
//		System.out.println();
//		System.out.println("Inside parseQuestionsWithOptionsMap");
//		System.out.println();
//		System.out.println();

		return questionsWithOptions;
	}
	
	
	public List<String[]> parseQuestionsWithOptionsList(String text){
		
		List<String[]> questionsWithOptions = new ArrayList<>();
		String[] lines = text.split("\n");
		String currentQuestion = "";
		String currentOptions = "";
		
		for(String line : lines) {
			if(line.matches("\\d+\\.(.*)")) {
				
				if(!currentQuestion.isEmpty() && !currentOptions.isEmpty()) {
					
					String[] queNOptions = {currentQuestion, currentOptions};
					questionsWithOptions.add(queNOptions);
				}
				
				currentQuestion = line.substring(line.indexOf('.') + 1).trim();
			}
			
			else if(line.matches("[a-d]\\.\\s*.*")) {
				
				currentOptions = line.substring(line.indexOf('.') - 1).trim();
			}
		}
		
		if(!currentQuestion.isEmpty() && !currentOptions.isEmpty()) {
			String[] queNOptions = {currentQuestion, currentOptions};
			questionsWithOptions.add(queNOptions);
		}
		//System.out.println("questinsWIthOptions: "+questionsWithOptions);
		
		//Print for testing purposes
//		for(String[] questionWithOptions: questionsWithOptions) {
//			
//			System.out.println("Question: "+questionWithOptions[0]);
//			System.out.println("Options: "+questionWithOptions[1]);
//			
//		}
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("Inside parseQuestionsWithOptionsList");
//		System.out.println();
//		System.out.println();
		
		//passed the output of this method to addQuesFromQuePaper method to further make it compatible to save it to DB
		addQuesFromQuePaper(questionsWithOptions);
		
		return questionsWithOptions;
	}
	
	public String extractLessonNameFromHeader(String headerText) {
		
		String lessonName = "";
		
		//Define the keyword that marks start of lesson name
		String lessonKeyword = "Lesson Name :-";
		
		//find the index of lessonKeyword
		int lessonKeywordIndex = headerText.indexOf(lessonKeyword);
		
		//if lesson keyword is found
		if(lessonKeywordIndex != -1) {
			//Extract the following text
			lessonName = headerText.substring(lessonKeywordIndex + lessonKeyword.length());
		
			//in case of any additional text after lesson name, remove it
			int nextLineIndex = lessonName.indexOf('\n');
			if(nextLineIndex != -1)
				lessonName = lessonName.substring(0, nextLineIndex);	
		}
//		System.out.println("lesson name: "+lessonName);
		
		
		return lessonName;
	}
	
	public int extractLessonNumberFromHeader(String headerText) {
	    int lessonNumber = -1; // Default value if lesson number is not found
	    
	    // Define the keyword that marks the start of the lesson name
	    String lessonKeyword = "Lesson Name :-";

	    // Find the index of the lesson keyword
	    int lessonKeywordIndex = headerText.indexOf(lessonKeyword);

	    // If the lesson keyword is found
	    if (lessonKeywordIndex != -1) {
	        // Extract the following text
	        String lessonInfo = headerText.substring(lessonKeywordIndex + lessonKeyword.length());

	        // Split the lesson info based on spaces
	        String[] parts = lessonInfo.split("\\s+");

	        // Iterate through the parts to find the lesson number
	        for (String part : parts) {
	            if (part.matches("\\d+\\..*")) { // Check if part starts with a number followed by a dot
	                // Extract the number before the dot and parse it as an integer
	                lessonNumber = Integer.parseInt(part.split("\\.")[0]);
	                break; // Exit loop once lesson number is found
	            }
	        }
	    }
//	    System.out.println("lesson number: " + lessonNumber);
	    return lessonNumber;
	}
////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void addQuesFromQuePaper(List<String[]> questionsWithOptions) {
		// TODO Auto-generated method stub
		List<Question> parsedQues = new ArrayList<Question>();
		
		for(String[] data : questionsWithOptions) {
			String question = data[0];
			String options = data[1];
			
			int lessonNumber = extractLessonNumberFromHeader(headerText);
			parsedQues.add(new Question(question, options, lessonNumber));
		}
		
//		System.out.println();
//		System.out.println();
//		System.out.println("Inside addQuesFromQuePaper");
//		System.out.println("Saved to DB");
//		System.out.println();
		
		wordReaderRepo.saveAll(parsedQues);
		//return parsedQues;
	}
	
	public List<Question> showAllQuestions(){
		return wordReaderRepo.findAll();
	}
	
}