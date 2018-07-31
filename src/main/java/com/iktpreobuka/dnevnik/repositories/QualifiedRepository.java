package com.iktpreobuka.dnevnik.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.QualifiedEntity;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;
import com.iktpreobuka.dnevnik.entities.TeacherEntity;

public interface QualifiedRepository extends CrudRepository<QualifiedEntity, Integer> {

	boolean existsByTeacherAndSubject(TeacherEntity teacher,SubjectEntity subject);

}
