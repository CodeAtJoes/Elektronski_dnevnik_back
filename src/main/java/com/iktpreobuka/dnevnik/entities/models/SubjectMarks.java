package com.iktpreobuka.dnevnik.entities.models;

import java.util.ArrayList;
import java.util.List;

import com.iktpreobuka.dnevnik.entities.MarkEntity;

public class SubjectMarks {
	
	private String subject;
	private List<MarkEntity> marks = new ArrayList<MarkEntity>();
	
	public SubjectMarks() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

	

	
	
	
	
	

}
