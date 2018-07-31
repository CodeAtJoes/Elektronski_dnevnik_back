package com.iktpreobuka.dnevnik.security.util;

import java.util.List;

import com.iktpreobuka.dnevnik.entities.MarkEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;

public class StudentMarks {
	
	StudentEntity student;
	List<MarkEntity> marks;
	public StudentMarks() {
		super();
		// TODO Auto-generated constructor stub
	}
	public StudentEntity getStudent() {
		return student;
	}
	public void setStudent(StudentEntity student) {
		this.student = student;
	}
	public List<MarkEntity> getMarks() {
		return marks;
	}
	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}
	
	

}
