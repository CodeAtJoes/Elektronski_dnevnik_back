package com.iktpreobuka.dnevnik.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.dnevnik.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity,Integer> {
	
	boolean existsByEmail(String email);
	UserEntity findByUsername(String username);

}
