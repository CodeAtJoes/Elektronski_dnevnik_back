package com.iktpreobuka.dnevnik.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.ParentEntity;

public interface ParentRepository extends CrudRepository<ParentEntity, Integer> {

	List<ParentEntity> findByLastNameStartingWith(String prezime);

}
