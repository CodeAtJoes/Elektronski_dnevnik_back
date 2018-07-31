package com.iktpreobuka.dnevnik.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.iktpreobuka.dnevnik.entities.AdminEntity;
import com.iktpreobuka.dnevnik.entities.dto.UpdateUserDTO;
import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.AdminRepository;
import com.iktpreobuka.dnevnik.services.UserDao;

@RestController
@RequestMapping(path="/register/admins")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class AdminController {
	
	@Autowired
	AdminRepository adminRep;
	
	@Autowired
	UserDao userDao;
	
	private final Logger logger= (Logger) LoggerFactory.getLogger(this.getClass());
	
	///////////////////////////////////GET ALL---SEARCH/////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll(@RequestParam(name="prezime",required=false) String prezime){
		try {
			if(prezime==null) {
				List<AdminEntity> admins=(List<AdminEntity>) adminRep.findAll();
				return new ResponseEntity<List<AdminEntity>>(admins,HttpStatus.OK);}
			else {
				List<AdminEntity> admins=(List<AdminEntity>) adminRep.findByLastNameStartingWith(prezime);
				return new ResponseEntity<List<AdminEntity>>(admins,HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////// ADD NEW ADMIN ////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> addNewAdmin(@Valid @RequestBody UserDTO newAdmin){
		
		return userDao.addNewUser(newAdmin, EUserRole.ROLE_ADMIN);
	}
	
	///////////////////////////////////// UPDATE ADMIN ////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{id}")
	public ResponseEntity<?> updateAdmin(@Valid @RequestBody UpdateUserDTO admin,@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(adminRep.existsById(Integer.parseInt(id))) {
				AdminEntity ae = adminRep.findById(Integer.parseInt(id)).get();
				if(admin.getFirstName()!=null) ae.setFirstName(admin.getFirstName());
				if(admin.getLastName()!=null) ae.setLastName(admin.getLastName());
				//if(admin.getUsername()!=null) ae.setUsername(admin.getUsername());
				if(admin.getEmail()!=null) ae.setEmail(admin.getEmail());
				
				logger.info("Admin with id: "+userDao.getLoggedInID()+" has changed admin profile with id:"+id+"!");
				logger.error("Error occured during the attempt to update admin profile.");
				return new ResponseEntity<AdminEntity>(adminRep.save(ae),HttpStatus.OK);
			}
			else
				return new ResponseEntity<RestError>(new RestError("Administrator with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////////// CHANGE PASSWORD ///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/changePassword/{id}")
	public ResponseEntity<?> changePassword(@PathVariable String id, @RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
				return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			return userDao.changePass(id, oldPassword, newPassword);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////// GET ADMIN BY ID /////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET,value="/{id}")
	public ResponseEntity<?> getAdminById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(adminRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<AdminEntity>(adminRep.findById(Integer.parseInt(id)).get(),HttpStatus.OK);
			else
				return new ResponseEntity<RestError>(new RestError("Administrator with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////// DELETE ADMIN //////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.DELETE ,value="/{id}")
	public ResponseEntity<?> deleteAdminById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(adminRep.existsById(Integer.parseInt(id))) {
				AdminEntity ae = adminRep.findById(Integer.parseInt(id)).get();
				adminRep.delete(ae);
				
				logger.info("Admin with id: "+userDao.getLoggedInID()+" has deleted admin profile with id:"+id+"!");
				return new ResponseEntity<AdminEntity>(ae,HttpStatus.OK);
			}
			else
				return new ResponseEntity<RestError>(new RestError("Administrator with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////// GET LOG ///////////////////////////////////////////////////////
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET ,value="/getLog")
	public ResponseEntity<?> getLog(){
		BufferedReader br=null;
		try {
			br= new BufferedReader(new FileReader("logs/spring-boot-logging.log"));
			String r;
			StringBuilder sb=new StringBuilder("");
			while((r=br.readLine()) != null)
				sb.append(r).append("\n");
			br.close();
			
			return new ResponseEntity<StringBuilder>(sb,HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
