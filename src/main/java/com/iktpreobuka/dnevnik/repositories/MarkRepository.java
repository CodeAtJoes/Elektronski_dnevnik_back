package com.iktpreobuka.dnevnik.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.MarkEntity;
import com.iktpreobuka.dnevnik.entities.MarkTypeEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;

public interface MarkRepository extends CrudRepository<MarkEntity, Integer> {
	
	boolean existsByStudentAndTimetableQualificationSubjectAndMarkType(StudentEntity student,SubjectEntity subject,MarkTypeEntity markType);
	List<MarkEntity> findByStudentAndTimetableQualificationSubject(StudentEntity student,SubjectEntity subject);
}
