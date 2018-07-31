package com.iktpreobuka.dnevnik.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.ClassEntity;
import com.iktpreobuka.dnevnik.entities.QualifiedEntity;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;
import com.iktpreobuka.dnevnik.entities.TeacherEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;

public interface TimetableRepository extends CrudRepository<TimetableEntity, Integer> {

	boolean existsByTtClassAndQualification(ClassEntity ce,QualifiedEntity qe);
	
	boolean existsByTtClassAndQualificationSubjectAndActive(ClassEntity ce,SubjectEntity se,boolean isActive);
	
	List<TimetableEntity> findByTtClassAndActive(ClassEntity cls,boolean isActive);
	
	List<TimetableEntity> findByQualificationTeacherAndActive(TeacherEntity te,boolean isActive);
	

}
