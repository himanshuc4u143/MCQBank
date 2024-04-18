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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WordReaderService {
	
	public String headerText;
	
	public void insertDataIntoString(String data) {
        // Insert data into the string variable
        headerText = data;
    }
	
	private List<Question> ques = new ArrayList<Question>();
	
	public List<Question> getAllQuestions() {
		System.out.println("inside getAllQuestions service");
        return ques;
    }
    
    public void addQuestions(List<Question> newQues) {
        ques.addAll(newQues);
    }

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
			insertDataIntoString(stringBuilder.toString()); 
			
			//Iterate through paras
			for(XWPFParagraph paragraph :  doc.getParagraphs()) {
				//System.out.println(paragraph.getText());
				stringBuilder.append(paragraph.getText());
				stringBuilder.append("\n");
			}
		
			//parseQuestionsWithOptions(stringBuilder.toString());
		}
		return stringBuilder.toString();
	}
	
	public Map<String, String> parseQuestionsWithOptions(String text){
		Map<String, String> questionsWithOptions = new HashMap<String, String>();
		String[] lines = text.split("\n");
		String currentQuestion = "";
		String currentOptions = "";
		
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
				currentOptions = line.substring(line.indexOf('.') + 1).trim();
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

		return questionsWithOptions;
	}
	
	public String extractLessonNameFromHeader(String headerText) {
		
		String lessonName = "";
		
		//Define the keyword that marks start of lesson name
		String lessonKeyword = "Lesson Name :-";
		
		//find the index of lessonKeyword
		int lessonKeywordIndex = headerText.indexOf(lessonKeyword);
		
		//if lesson keyowrd is found
		if(lessonKeywordIndex != -1) {
			//Extract the following text
			lessonName = headerText.substring(lessonKeywordIndex + lessonKeyword.length());
		
			//in case of any additional text after lesson name, remove it
			int nextLineIndex = lessonName.indexOf('\n');
			if(nextLineIndex != -1)
				lessonName = lessonName.substring(0, nextLineIndex);	
		}
		System.out.println("lesson name: "+lessonName);
		return lessonName;
	}
	
}