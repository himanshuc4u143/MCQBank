//Question.java

package com.fileParser;

import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "all_questions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long queId;
	
	@Column(nullable = false)
	private String que;
	
	@Column(nullable = false)
	private String options;
	
//	@Column(nullable = false)
	private int lessonNumber;
	
	//private String lessonName;

	public Question(String que, String options, int lessonNumber) {
		super();
		this.que = que;
		this.options = options;
		this.lessonNumber = lessonNumber;
	}
	
	
	
	
	
}
