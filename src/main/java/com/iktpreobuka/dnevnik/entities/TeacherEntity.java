package com.iktpreobuka.dnevnik.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TeacherEntity extends UserEntity{
	
	@JsonIgnore
	@OneToMany(mappedBy="teacher",fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	private List<QualifiedEntity> qualifications=new ArrayList<>();

	public TeacherEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<QualifiedEntity> getQualifications() {
		return qualifications;
	}

	public void setQualifications(List<QualifiedEntity> qualifications) {
		this.qualifications = qualifications;
	}
	
	

}
