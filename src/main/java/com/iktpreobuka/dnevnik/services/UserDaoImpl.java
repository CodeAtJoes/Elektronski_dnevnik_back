 package com.iktpreobuka.dnevnik.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.iktpreobuka.dnevnik.controllers.AuthController;
import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.AdminEntity;
import com.iktpreobuka.dnevnik.entities.ParentEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.TeacherEntity;
import com.iktpreobuka.dnevnik.entities.UserEntity;
import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.AdminRepository;
import com.iktpreobuka.dnevnik.repositories.ParentRepository;
import com.iktpreobuka.dnevnik.repositories.StudentRepository;
import com.iktpreobuka.dnevnik.repositories.TeacherRepository;
import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.security.util.Encryption;;
@Service
public class UserDaoImpl implements UserDao{
	
	@Autowired
	AdminRepository adminRep;
	
	@Autowired
	ParentRepository parentRep;
	
	@Autowired
	StudentRepository studentRep;
	
	@Autowired
	TeacherRepository teacherRep;
	
	@Autowired
	UserRepository userRep;
	
	@Override
	public ResponseEntity<?> addNewUser(UserDTO newUser, EUserRole role) {
		try {
			UserEntity user=null;
			switch(role) {
				
			case ROLE_ADMIN: {user=new AdminEntity();
						break;}
			case ROLE_PARENT: {user= new ParentEntity();
						 break;}
			case ROLE_STUDENT:{user =new StudentEntity();
						 break;}
			case ROLE_TEACHER:{user=new TeacherEntity();}
			}
			if(userRep.existsByEmail(newUser.getEmail()))
				return new ResponseEntity<RestError>(new RestError("There is already an user with the provided email."),HttpStatus.BAD_REQUEST);
			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setUsername(newUser.getEmail());
			user.setEmail(newUser.getEmail());
			user.setPassword(Encryption.getPassEncoded(newUser.getPassword()));
			user.setUserRole(role);
			
			switch(role) {
			case ROLE_ADMIN: {return new ResponseEntity<AdminEntity>(adminRep.save((AdminEntity)user),HttpStatus.OK);}
			case ROLE_PARENT: return new ResponseEntity<ParentEntity>(parentRep.save((ParentEntity)user),HttpStatus.OK);
			case ROLE_STUDENT: return new ResponseEntity<StudentEntity>(studentRep.save((StudentEntity) user),HttpStatus.OK);
			case ROLE_TEACHER:return new ResponseEntity<TeacherEntity>(teacherRep.save((TeacherEntity)user),HttpStatus.OK);
			}
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	@Override
	public ResponseEntity<?> changePass(String id, String oldPass, String newPass) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!userRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("User with the requested id doesn't exist."),HttpStatus.NOT_FOUND);
			if(!Encryption.matchPass(oldPass, userRep.findById(Integer.parseInt(id)).get().getPassword()))
				return new ResponseEntity<RestError>(new RestError("Old password doesn't match the actual password."),HttpStatus.BAD_REQUEST);
			
			UserEntity user=userRep.findById(Integer.parseInt(id)).get();
			user.setPassword(Encryption.getPassEncoded(newPass));
			switch(user.getUserRole()) {
			case ROLE_ADMIN: return new ResponseEntity<AdminEntity>(adminRep.save((AdminEntity)user),HttpStatus.OK);
			case ROLE_PARENT: return new ResponseEntity<ParentEntity>(parentRep.save((ParentEntity)user),HttpStatus.OK);
			case ROLE_STUDENT: return new ResponseEntity<StudentEntity>(studentRep.save((StudentEntity) user),HttpStatus.OK);
			case ROLE_TEACHER:return new ResponseEntity<TeacherEntity>(teacherRep.save((TeacherEntity)user),HttpStatus.OK);
			}
			return null;
			
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

	@Override
	public Integer getLoggedInID() {
		Integer id = userRep.findByUsername(AuthController.getLoggedInUsername()).getId();
		return id;
	} 
	
}
