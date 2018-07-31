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
public class SubjectEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private Integer weekLoad;
	@Version
	private Integer version;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name="grade")
	private GradeEntity grade;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name="subject")
	private SubjectTitleEntity subject;
	
	@JsonIgnore
	@OneToMany(mappedBy="subject",fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	private List<QualifiedEntity> qualified_teachers=new ArrayList<>();

	public SubjectEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWeekLoad() {
		return weekLoad;
	}

	public void setWeekLoad(Integer weekLoad) {
		this.weekLoad = weekLoad;
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

	public List<QualifiedEntity> getQualified_teachers() {
		return qualified_teachers;
	}

	public void setQualified_teachers(List<QualifiedEntity> qualified_teachers) {
		this.qualified_teachers = qualified_teachers;
	}

	public SubjectTitleEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectTitleEntity subject) {
		this.subject = subject;
	}
	
	
	

}
