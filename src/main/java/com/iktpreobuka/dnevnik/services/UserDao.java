package com.iktpreobuka.dnevnik.services;

import org.springframework.http.ResponseEntity;

import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;

public interface UserDao {
	
	public ResponseEntity<?> addNewUser(UserDTO newUser,EUserRole role);
	
	public ResponseEntity<?> changePass(String id,String oldPass,String newPass);
	

	public Integer getLoggedInID();
}
