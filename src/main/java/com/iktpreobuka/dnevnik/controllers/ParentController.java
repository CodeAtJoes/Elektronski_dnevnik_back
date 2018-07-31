package com.iktpreobuka.dnevnik.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.ParentEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.dto.UpdateUserDTO;
import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.ParentRepository;
import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.services.UserDao;

@RestController
@RequestMapping(path="/register/parents")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class ParentController {
	
	@Autowired
	ParentRepository parentRep;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	UserRepository userRep;
	
	/////////////////////////////////GET ALL---SEARCH/////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll(@RequestParam(name="prezime",required=false) String prezime){
		try {
			if(prezime==null) {
				List<ParentEntity> parents=(List<ParentEntity>) parentRep.findAll();
				return new ResponseEntity<List<ParentEntity>>(parents,HttpStatus.OK);}
			else {
				List<ParentEntity> parents=(List<ParentEntity>) parentRep.findByLastNameStartingWith(prezime)/*.stream().map(admin -> new AdminEntity(admin))
						.collect(Collectors.toList())*/;
				return new ResponseEntity<List<ParentEntity>>(parents,HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////////////ADD NEW PARENT///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> addNewParent(@Valid @RequestBody UserDTO newParent){
		
		return userDao.addNewUser(newParent, EUserRole.ROLE_PARENT);
	}
	
	//////////////////////////////////////////UPDATE PARENT ////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{id}")
	public ResponseEntity<?> updateParent(@Valid @RequestBody UpdateUserDTO parent,@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(parentRep.existsById(Integer.parseInt(id))) {
				ParentEntity pe = parentRep.findById(Integer.parseInt(id)).get();
				if(parent.getFirstName()!=null) pe.setFirstName(parent.getFirstName());
				if(parent.getLastName()!=null) pe.setLastName(parent.getLastName());
				//if(parent.getUsername()!=null) pe.setUsername(parent.getUsername());
				if(parent.getEmail()!=null) pe.setEmail(parent.getEmail());
				return new ResponseEntity<ParentEntity>(parentRep.save(pe),HttpStatus.OK);
			}
			else
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////CHANGE PASSWORD/////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_PARENT"})
	@RequestMapping(method=RequestMethod.PUT,value="/changePassword/{id}")
	public ResponseEntity<?> changePassword(@PathVariable String id, @RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!parentRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_PARENT)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			return userDao.changePass(id, oldPassword, newPassword);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////GET PARENT BY ID/////////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_PARENT"})
	@RequestMapping(method=RequestMethod.GET,value="/{id}")
	public ResponseEntity<?> getParentById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!parentRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_PARENT)) {
				if(!(Integer.valueOf(id).equals((userDao.getLoggedInID()))))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<ParentEntity>(parentRep.findById(Integer.parseInt(id)).get(),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////////DELETE PARENT BY ID///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.DELETE ,value="/{id}")
	public ResponseEntity<?> deleteParentById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST); 
			if(parentRep.existsById(Integer.parseInt(id))) {
				ParentEntity pe = parentRep.findById(Integer.parseInt(id)).get();
				if(pe.getChildren().isEmpty()) {
					parentRep.delete(pe);
					return new ResponseEntity<ParentEntity>(pe,HttpStatus.OK);
				}
				else {
					return new ResponseEntity<RestError>(new RestError("Parent with the provided id cannot be deleted, because it is attached with some student(s)."),HttpStatus.FORBIDDEN);
				}
			}
			else
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////GET STUDENTS BY PARENT ID//////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_PARENT"})
	@RequestMapping(method=RequestMethod.GET,value="/getChildren/{id}")
	public ResponseEntity<?> getChildernByParent(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!parentRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_PARENT)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			List<StudentEntity>	children = parentRep.findById(Integer.parseInt(id)).get().getChildren();
			return new ResponseEntity<List<StudentEntity>>(children,HttpStatus.OK);
				
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
