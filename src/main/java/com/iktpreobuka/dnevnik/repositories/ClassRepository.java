package com.iktpreobuka.dnevnik.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.ClassEntity;
import com.iktpreobuka.dnevnik.entities.GradeEntity;

public interface ClassRepository extends CrudRepository<ClassEntity, Integer> {

	boolean existsByGradeAndIndeks(GradeEntity grade, Integer indeks);

}
