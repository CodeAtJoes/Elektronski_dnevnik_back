package com.iktpreobuka.dnevnik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iktpreobuka.dnevnik.entities.MarkEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;
import com.iktpreobuka.dnevnik.repositories.MarkRepository;

@Service
public class MarkDaoImpl implements MarkDao {
	
	@Autowired
	MarkRepository markRep;

	@Override
	public Integer makeFinal(StudentEntity se, TimetableEntity te) {
		double sum=0;
		double result;
		Integer markValue;
		List<MarkEntity> marks= markRep.findByStudentAndTimetableQualificationSubject(se, te.getQualification().getSubject());
		
		for(MarkEntity me: marks)
			sum+=me.getMarkValue();
		
		if(marks.size()>0)
			result=sum/marks.size();
		else
			result=1;
		
		if(result>=4.5)
			markValue=5;
		else if(result>=3.5)
			markValue=4;
		else if(result>=2.5)
			markValue=3;
		else if(result>=1.5)
			markValue=2;
		else
			markValue=1;
		
		return markValue;
	}

}
