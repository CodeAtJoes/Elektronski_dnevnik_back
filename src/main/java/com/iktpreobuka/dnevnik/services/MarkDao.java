package com.iktpreobuka.dnevnik.services;

import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;

public interface MarkDao {
	
	public Integer makeFinal(StudentEntity se,TimetableEntity te);

}
