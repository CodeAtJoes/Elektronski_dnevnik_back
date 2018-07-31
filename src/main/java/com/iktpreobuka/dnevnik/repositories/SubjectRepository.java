package com.iktpreobuka.dnevnik.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.GradeEntity;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;
import com.iktpreobuka.dnevnik.entities.SubjectTitleEntity;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {


	boolean existsByGradeAndSubject(GradeEntity grade, SubjectTitleEntity subject);
	
}

