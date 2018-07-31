package com.iktpreobuka.dnevnik.controllers;

import java.util.ArrayList;
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
import com.iktpreobuka.dnevnik.entities.MarkEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;
import com.iktpreobuka.dnevnik.entities.dto.UpdateUserDTO;
import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.entities.models.SubjectMarks;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.ClassRepository;
import com.iktpreobuka.dnevnik.repositories.MarkRepository;
import com.iktpreobuka.dnevnik.repositories.ParentRepository;
import com.iktpreobuka.dnevnik.repositories.StudentRepository;
import com.iktpreobuka.dnevnik.repositories.TimetableRepository;
import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.services.UserDao;


@RestController
@RequestMapping(path="/register/students")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class StudentController {
	
	@Autowired
	StudentRepository studentRep;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	ParentRepository parentRep;
	
	@Autowired
	ClassRepository classRep;
	
	@Autowired
	TimetableRepository timetableRep;
	
	@Autowired
	MarkRepository markRepository;
	
	@Autowired
	UserRepository userRep;
	
	//////////////////////////////////////GET ALL----SEARCH/////////////////////////////////////////
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll(@RequestParam(name="prezime",required=false) String prezime){
		try {
			if(prezime==null) {
				List<StudentEntity> students=(List<StudentEntity>) studentRep.findAll();
				return new ResponseEntity<List<StudentEntity>>(students,HttpStatus.OK);}
			else {
				List<StudentEntity> students=(List<StudentEntity>) studentRep.findByLastNameStartingWith(prezime)/*.stream().map(admin -> new AdminEntity(admin))
						.collect(Collectors.toList())*/;
				return new ResponseEntity<List<StudentEntity>>(students,HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////ADD NEW STUDENT//////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> addNewStudent(@Valid @RequestBody UserDTO newStudent){
		return userDao.addNewUser(newStudent, EUserRole.ROLE_STUDENT);
		
	}
	
	/////////////////////////////////////UPDATE STUDENT////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{id}")
	public ResponseEntity<?> updateStudent(@Valid @RequestBody UpdateUserDTO student,@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST); 
			if(studentRep.existsById(Integer.parseInt(id))) {
				StudentEntity se = studentRep.findById(Integer.parseInt(id)).get();
				if(student.getFirstName()!=null) se.setFirstName(student.getFirstName());
				if(student.getLastName()!=null) se.setLastName(student.getLastName());
				//if(student.getUsername()!=null) se.setUsername(student.getUsername());
				if(student.getEmail()!=null) se.setEmail(student.getEmail());
				return new ResponseEntity<StudentEntity>(studentRep.save(se),HttpStatus.OK);
			}
			else
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////////CHANGE PASSWORD/////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_STUDENT"})
	@RequestMapping(method=RequestMethod.PUT,value="/changePassword/{id}")
	public ResponseEntity<?> changePassword(@PathVariable String id, @RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("User with the requested id doesn't exist."),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_STUDENT)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			return userDao.changePass(id, oldPassword, newPassword);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////GET STUDENT BY ID//////////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_STUDENT"})
	@RequestMapping(method=RequestMethod.GET,value="/{id}")
	public ResponseEntity<?> getStudentById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_STUDENT)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<StudentEntity>(studentRep.findById(Integer.parseInt(id)).get(),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////////DELETE STUDENT//////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.DELETE ,value="/{id}")
	public ResponseEntity<?> deleteStudentById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
			
			StudentEntity se = studentRep.findById(Integer.parseInt(id)).get();
			if((se.getStClass()!=null)||!se.getMarks().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Student is attached with some entities and cannot be deleted."),HttpStatus.BAD_REQUEST);
			
			studentRep.delete(se);
			return new ResponseEntity<StudentEntity>(se,HttpStatus.OK);
			
				
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////ATTACH PARENT TO STUDENT//////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{studentId}/parent/{parentId}")
	public ResponseEntity<?> attachParent(@PathVariable String studentId,@PathVariable String parentId){
		try {
			if(!studentId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Student id must be integer."),HttpStatus.BAD_REQUEST);
			if(!parentId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Parent id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(studentId)))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
			if(!parentRep.existsById(Integer.parseInt(parentId)))
				return new ResponseEntity<RestError>(new RestError("Parent with the provided id not found"),HttpStatus.NOT_FOUND);
			StudentEntity se=studentRep.findById(Integer.parseInt(studentId)).get();
			se.setParent(parentRep.findById(Integer.parseInt(parentId)).get());
			return new ResponseEntity<StudentEntity>(studentRep.save(se),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	////////////////////////////////////////ADD STUDENT TO CLASS///////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{studentId}/class/{classId}")
	public ResponseEntity<?> addStudentToClass(@PathVariable String studentId,@PathVariable String classId){
		try {
			if(!studentId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Student id must be integer."),HttpStatus.BAD_REQUEST);
			if(!classId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Class id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(studentId)))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
			if(!classRep.existsById(Integer.parseInt(classId)))
				return new ResponseEntity<RestError>(new RestError("Class with the provided id not found"),HttpStatus.NOT_FOUND);
			StudentEntity se=studentRep.findById(Integer.parseInt(studentId)).get();
			se.setStClass(classRep.findById(Integer.parseInt(classId)).get());
			return new ResponseEntity<StudentEntity>(studentRep.save(se),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/////////////////////////////////////////GET SUBJECT:MARKS BY STUDENT ID/////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_STUDENT","ROLE_PARENT"})
	@RequestMapping(method=RequestMethod.GET,value="/subjectMarks/{id}")
	public ResponseEntity<?> getSubjectMarksByStudent(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Student with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_STUDENT)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			
			StudentEntity stud = studentRep.findById(Integer.parseInt(id)).get();
			
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_PARENT)) {
				if(!stud.getParent().getId().equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			
			List<SubjectMarks> subjectMarks = new ArrayList<SubjectMarks>();
			
			for(TimetableEntity te: timetableRep.findByTtClassAndActive(stud.getStClass(), true)) {
				SubjectEntity subj=te.getQualification().getSubject();
				SubjectMarks subMar = new SubjectMarks();
				subMar.setSubject(subj.getSubject().getTitle());
				List<MarkEntity> marks = new ArrayList<MarkEntity>();
				for(MarkEntity me: markRepository.findByStudentAndTimetableQualificationSubject(stud, subj)) {
					marks.add(me);
				}
				subMar.setMarks(marks);
				subjectMarks.add(subMar);
			}
			return new ResponseEntity<List<SubjectMarks>>(subjectMarks,HttpStatus.OK);
			
				
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
