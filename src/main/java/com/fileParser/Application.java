package com.fileParser;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

//	private final WordReaderService wordReaderService;
//	
//	public Application(WordReaderService wordReaderService) {
//		this.wordReaderService = wordReaderService;
//	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean // equivalent to <bean id ..../> in xml file
	public ModelMapper mapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
	
//	@Bean
//    public CommandLineRunner run() {
//        return args -> {
//            wordReaderService.readWordFile("/home/himanshu/Downloads/_ English med. Sci1,Sem-ll Ch- 10.docx");
//        };
//
//	}
}
