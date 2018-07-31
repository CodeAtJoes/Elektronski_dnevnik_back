 package com.iktpreobuka.dnevnik.controllers;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.iktpreobuka.dnevnik.entities.QualifiedEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.TeacherEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;
import com.iktpreobuka.dnevnik.entities.dto.UpdateUserDTO;
import com.iktpreobuka.dnevnik.entities.dto.UserDTO;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.MarkRepository;
import com.iktpreobuka.dnevnik.repositories.QualifiedRepository;
import com.iktpreobuka.dnevnik.repositories.SubjectRepository;
import com.iktpreobuka.dnevnik.repositories.TeacherRepository;
import com.iktpreobuka.dnevnik.repositories.TimetableRepository;
import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.security.util.StudentMarks;
import com.iktpreobuka.dnevnik.services.UserDao;

@RestController
@RequestMapping(path="/register/teachers")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class TeacherController {
	
	@Autowired
	TeacherRepository teacherRep;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	SubjectRepository subjectRep;
	
	@Autowired
	QualifiedRepository qualifiedRep;
	
	@Autowired
	TimetableRepository timetableRep;
	
	@Autowired 
	MarkRepository markRep;
	
	@Autowired
	UserRepository userRep;
	
	////////////////////////////////////////GET ALL TEACHERS--SEARCH////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll(@RequestParam(name="prezime",required=false) String prezime){
		try {
			if(prezime==null) {
				List<TeacherEntity> teachers=(List<TeacherEntity>) teacherRep.findAll();
				return new ResponseEntity<List<TeacherEntity>>(teachers,HttpStatus.OK);}
			else {
				List<TeacherEntity> teachers=(List<TeacherEntity>) teacherRep.findByLastNameStartingWith(prezime);
				return new ResponseEntity<List<TeacherEntity>>(teachers,HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////ADD NEW TEACHER////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody UserDTO newTeacher){
		
		return userDao.addNewUser(newTeacher, EUserRole.ROLE_TEACHER);
	}
	
	/////////////////////////////////////////UPDATE TEACHER///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT,value="/{id}")
	public ResponseEntity<?> updateTeacher(@Valid @RequestBody UpdateUserDTO teacher,@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(teacherRep.existsById(Integer.parseInt(id))) {
				TeacherEntity te = teacherRep.findById(Integer.parseInt(id)).get();
				if(teacher.getFirstName()!=null) te.setFirstName(teacher.getFirstName());
				if(teacher.getLastName()!=null) te.setLastName(teacher.getLastName());
				//if(teacher.getUsername()!=null) te.setUsername(teacher.getUsername());
				if(teacher.getEmail()!=null) te.setEmail(teacher.getEmail());
				return new ResponseEntity<TeacherEntity>(teacherRep.save(te),HttpStatus.OK);
			}
			else
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////CHANGE PASSWORD///////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.PUT,value="/changePassword/{id}")
	public ResponseEntity<?> changePassword(@PathVariable String id, @RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
					if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
						return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
				}
				return userDao.changePass(id, oldPassword, newPassword);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
	
	/////////////////////////////////GET TEACHER BY ID//////////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.GET,value="/{id}")
	public ResponseEntity<?> getTeacherById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<TeacherEntity>(teacherRep.findById(Integer.parseInt(id)).get(),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////DELETE TEACHER//////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.DELETE ,value="/{id}")
	public ResponseEntity<?> deleteTeachertById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			TeacherEntity te = teacherRep.findById(Integer.parseInt(id)).get();
			if(!te.getQualifications().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Teacher cannot be deleted."),HttpStatus.BAD_REQUEST);
			teacherRep.delete(te);
			return new ResponseEntity<TeacherEntity>(te,HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////ADD QUALIFICATIONS TO TEACHER BY TEACHER ID///////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST,value="/{id}/addQualifications")
	public ResponseEntity<?> addQualifications(@PathVariable String id,@RequestParam(name="subjectIds") String[] subjectIds){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Teacher id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			TeacherEntity te = teacherRep.findById(Integer.parseInt(id)).get();
			List<QualifiedEntity> existing=new ArrayList<QualifiedEntity>();
			List<QualifiedEntity> saved=new ArrayList<QualifiedEntity>();
			List<QualifiedEntity> attempted=new ArrayList<QualifiedEntity>();
			for(int i=0;i<subjectIds.length;i++) {
				if(!subjectIds[i].matches("\\d+"))
					return new ResponseEntity<RestError>(new RestError("Subject id(s) must be integer."),HttpStatus.BAD_REQUEST);
				QualifiedEntity qe=new QualifiedEntity();
				if(subjectRep.existsById(Integer.parseInt(subjectIds[i]))) {
					qe.setTeacher(te);
					qe.setSubject(subjectRep.findById(Integer.parseInt(subjectIds[i])).get());
					if(qualifiedRep.existsByTeacherAndSubject(te,qe.getSubject()))
						existing.add(qe);
					else {
						qualifiedRep.save(qe);
						saved.add(qe);
					}
				}
				else
					attempted.add(qe);
			}
			HashMap<String,List<QualifiedEntity>> map=new HashMap<String,List<QualifiedEntity>>();
			map.put("Already existing:",existing );
			map.put("Saved qualifications:", saved);
			map.put("Attempted to save qualifications with the non existing subjects", attempted);
			return new ResponseEntity<HashMap<String,List<QualifiedEntity>>>(map,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//TREBA LI UOPSTE???
	/*@RequestMapping(method=RequestMethod.PUT,value="/{id}")
	public ResponseEntity<?> updateQualifications(@PathVariable String id,@RequestParam(name="subjectIds") String[] subjectIds){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Teacher id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			TeacherEntity te = teacherRep.findById(Integer.parseInt(id)).get();
			 
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	////////////////////////////////////GET ALL QUALIFICATIONS////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET,value="/qualifications")
	public ResponseEntity<?> getAllQualifications(){
		try {
			List<QualifiedEntity> qualifications=(List<QualifiedEntity>) qualifiedRep.findAll();
			return new ResponseEntity<List<QualifiedEntity>>(qualifications,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////GET QUALIFICATIONS BY Q-ID/////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET,value="/qualifications/{id}")
	public ResponseEntity<?> getQualificationById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Qualification id must be integer."),HttpStatus.BAD_REQUEST);
			if(!qualifiedRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Qualification with the provided id not found"),HttpStatus.NOT_FOUND);
			
			return new ResponseEntity<QualifiedEntity>(qualifiedRep.findById(Integer.parseInt(id)).get(),HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////DELETE QUALIFICATION BT Q-ID///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.DELETE ,value="/qualifications/{id}")
	public ResponseEntity<?> deleteQualificationById(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Qualification id must be integer."),HttpStatus.BAD_REQUEST);
			if(!qualifiedRep.existsById(Integer.parseInt(id))) 
				return new ResponseEntity<RestError>(new RestError("Qualification with the provided id not found"),HttpStatus.NOT_FOUND);
			QualifiedEntity qe = qualifiedRep.findById(Integer.parseInt(id)).get();
			if(!qe.getTimetable_classes().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Qualification cannot be deleted."),HttpStatus.BAD_REQUEST);
			qualifiedRep.delete(qe);
			return new ResponseEntity<QualifiedEntity>(qe,HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////GET TIMETABLES BY TEACHER ID///////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.GET,value="/getTimetables/{id}")
	public ResponseEntity<?> getTimetablesByTeacherId(@PathVariable String id){
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!teacherRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Teacher with the provided id not found"),HttpStatus.NOT_FOUND);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!Integer.valueOf(id).equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			List<TimetableEntity> timetables=timetableRep.findByQualificationTeacherAndActive(teacherRep.findById(Integer.parseInt(id)).get(), true);
			return new ResponseEntity<List<TimetableEntity>>(timetables,HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////GET STUDENT:MARKS BY TIMETABLE ID///////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.GET,value="/getStudentMarks/{ttId}")
	public ResponseEntity<?> getStudentMarksByTimetableId(@PathVariable String ttId){
		try {
			if(!ttId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(ttId)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id not found"),HttpStatus.NOT_FOUND);
			
			TimetableEntity te=timetableRep.findById(Integer.parseInt(ttId)).get();
			
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!te.getQualification().getTeacher().getId().equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			if(!te.isActive())
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id not is not active"),HttpStatus.BAD_REQUEST);
			List<StudentMarks> studMark=new ArrayList<StudentMarks>();
			for(StudentEntity se: te.getTtClass().getStudents()) {
				StudentMarks sm= new StudentMarks();
				sm.setStudent(se);
				List<MarkEntity> marks=new ArrayList<MarkEntity>();
				marks=markRep.findByStudentAndTimetableQualificationSubject(se, te.getQualification().getSubject());
				sm.setMarks(marks);
				studMark.add(sm);
			}
			return new ResponseEntity<List<StudentMarks>>(studMark,HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
