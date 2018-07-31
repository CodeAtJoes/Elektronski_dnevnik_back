package com.iktpreobuka.dnevnik.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.MarkTypeEntity;

public interface MarkTypeRepository extends CrudRepository<MarkTypeEntity, Integer> {
	
	MarkTypeEntity findByType(String type);

}
