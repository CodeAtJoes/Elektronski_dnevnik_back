package com.iktpreobuka.dnevnik.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClassEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer indeks;
	
	@Version
	private Integer version;
	
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	@JoinColumn(name="grade")
	private GradeEntity grade;
	
	@JsonIgnore
	@OneToMany(mappedBy="stClass",fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	private List<StudentEntity> students=new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy="ttClass",fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	private List<TimetableEntity> timetable_qualifications=new ArrayList<>();
	
	public ClassEntity() {
		super();
		// TODO Auto-generated constructor stub 
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIndeks() {
		return indeks;
	}

	public void setIndeks(Integer indeks) {
		this.indeks = indeks;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public GradeEntity getGrade() {
		return grade;
	}

	public void setGrade(GradeEntity grade) {
		this.grade = grade;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public List<TimetableEntity> getTimetable_qualifications() {
		return timetable_qualifications;
	}

	public void setTimetable_qualifications(List<TimetableEntity> timetable_qualifications) {
		this.timetable_qualifications = timetable_qualifications;
	}
	
	
}
