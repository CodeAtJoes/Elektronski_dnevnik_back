package com.iktpreobuka.dnevnik.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.services.UserDao;

@RestController
@RequestMapping("register/auth/")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class AuthController {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	UserRepository userRep;
	
	@RequestMapping(method=RequestMethod.POST, value = "login")
	public ResponseEntity<Object> login() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = auth.getAuthorities().iterator().next().getAuthority();
		String username=auth.getName();
		String id =""+ userRep.findByUsername(username).getId();
		
		return new ResponseEntity<>("{\"id\":\""+id+"\",\"role\":\"" + role + "\"}", HttpStatus.OK);
	}
	
	public static String getLoggedInUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username=auth.getName();
		return username;
		
	}

}
