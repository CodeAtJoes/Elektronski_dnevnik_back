package com.iktpreobuka.dnevnik.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.MarkEntity;
import com.iktpreobuka.dnevnik.entities.StudentEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;
import com.iktpreobuka.dnevnik.entities.dto.MarkDTO;
import com.iktpreobuka.dnevnik.entities.dto.MarkDTOF;
import com.iktpreobuka.dnevnik.enumerations.EUserRole;
import com.iktpreobuka.dnevnik.repositories.MarkRepository;
import com.iktpreobuka.dnevnik.repositories.MarkTypeRepository;
import com.iktpreobuka.dnevnik.repositories.StudentRepository;
import com.iktpreobuka.dnevnik.repositories.TimetableRepository;
import com.iktpreobuka.dnevnik.repositories.UserRepository;
import com.iktpreobuka.dnevnik.services.EmailService;
import com.iktpreobuka.dnevnik.services.MarkDao;
import com.iktpreobuka.dnevnik.services.UserDao;

@RestController
@RequestMapping(path="/register/marks")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class MarkController {
	
	@Autowired 
	MarkRepository markRep;
	
	@Autowired 
	StudentRepository studentRep;
	
	@Autowired 
	MarkTypeRepository markTypeRep;
	
	@Autowired 
	TimetableRepository timetableRep;
	
	@Autowired
	EmailService emailService;
	
	@Autowired 
	MarkDao markDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	UserRepository userRep;
	
	private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	//////////////////////////////////GET ALL//////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			List<MarkEntity> marks = (List<MarkEntity>) markRep.findAll();
			return new ResponseEntity<List<MarkEntity>>(marks, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////POST NEW MARK///////////////////////////////////////////// 
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createMark(@Valid@RequestBody MarkDTO mark) {
		try {
			if(!markTypeRep.existsById(Integer.parseInt(mark.getMarkTypeId())))
				return new ResponseEntity<RestError>(new RestError("Mark type with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(mark.getStudentId())))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(mark.getTimetableId())))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			
			StudentEntity se=studentRep.findById(Integer.parseInt(mark.getStudentId())).get();
			TimetableEntity te=timetableRep.findById(Integer.parseInt(mark.getTimetableId())).get();
			
			if(!te.isActive())
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id isn't active."),HttpStatus.BAD_REQUEST);
			if(!te.getTtClass().equals(se.getStClass()))
				return new ResponseEntity<RestError>(new RestError("Timetable's class doesn't match student's class."),HttpStatus.BAD_REQUEST);
			if(markRep.existsByStudentAndTimetableQualificationSubjectAndMarkType(se, te.getQualification().getSubject(), markTypeRep.findByType("zakljucna")))
				return new ResponseEntity<RestError>(new RestError("Unable to post new mark,there is already final mark."),HttpStatus.BAD_REQUEST);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!te.getQualification().getTeacher().getId().equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			
			MarkEntity me=new MarkEntity();
			me.setMarkValue(Integer.parseInt(mark.getMarkValue()));
			me.setMarkType(markTypeRep.findById(Integer.parseInt(mark.getMarkTypeId())).get());
			LocalDate date=LocalDate.parse(mark.getMarkEarned(), dtf);
			me.setMarkEarned(date);
			me.setMarkNoted(new Date());
			me.setNote(mark.getNote());
			me.setStudent(se);
			me.setTimetable(te);
			markRep.save(me);
			
			emailService.sendTemplateMessage(me);
			
			return new ResponseEntity<MarkEntity>(me,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////////////UPADTE MARK/////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.PUT, value="/{id}")
	public ResponseEntity<?> upateMark(@Valid@RequestBody MarkDTO mark,@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			if(!markTypeRep.existsById(Integer.parseInt(mark.getMarkTypeId())))
				return new ResponseEntity<RestError>(new RestError("Mark type with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			if(!studentRep.existsById(Integer.parseInt(mark.getStudentId())))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(mark.getTimetableId())))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			
			StudentEntity se=studentRep.findById(Integer.parseInt(mark.getStudentId())).get();
			TimetableEntity te=timetableRep.findById(Integer.parseInt(mark.getTimetableId())).get();
			
			if(!te.isActive())
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id isn't active."),HttpStatus.BAD_REQUEST);
			if(!te.getTtClass().equals(se.getStClass()))
				return new ResponseEntity<RestError>(new RestError("Timetable's class doesn't match student's class."),HttpStatus.BAD_REQUEST);
			if(markRep.existsByStudentAndTimetableQualificationSubjectAndMarkType(se, te.getQualification().getSubject(), markTypeRep.findByType("zakljucna")))
				return new ResponseEntity<RestError>(new RestError("Unable to update mark,there is already final mark."),HttpStatus.BAD_REQUEST);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!te.getQualification().getTeacher().getId().equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			
			MarkEntity me=markRep.findById(Integer.parseInt(id)).get();
			me.setMarkValue(Integer.parseInt(mark.getMarkValue()));
			me.setMarkType(markTypeRep.findById(Integer.parseInt(mark.getMarkTypeId())).get());
			LocalDate date=LocalDate.parse(mark.getMarkEarned(), dtf);
			me.setMarkEarned(date);
			me.setMarkNoted(new Date());
			me.setNote("OCENA JE PROMENJENA! - "+mark.getNote());
			me.setStudent(se);
			me.setTimetable(te);
			markRep.save(me);
			
			emailService.sendTemplateMessage(me);
			
			return new ResponseEntity<MarkEntity>(me,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////UPDATE MARK--FRONT//////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.PUT, value="/front/{id}")
	public ResponseEntity<?> upateMarkFront(@RequestBody MarkDTOF mark,@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			if(!markTypeRep.existsById(mark.getMarkTypeId()))
				return new ResponseEntity<RestError>(new RestError("Mark type with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			
			
			MarkEntity me=markRep.findById(Integer.parseInt(id)).get();
			me.setMarkValue((mark.getMarkValue()));
			me.setMarkType(markTypeRep.findById(mark.getMarkTypeId()).get());
			String str=mark.getMarkEarned();
			String[] ns=str.split("-");
			String string=ns[2]+"-"+ns[1]+"-"+ns[0];
			LocalDate date=LocalDate.parse(string, dtf);
			me.setMarkEarned(date);
			me.setMarkNoted(new Date());
			me.setNote("OCENA JE PROMENJENA! - "+mark.getNote());
			
			markRep.save(me);
			
			emailService.sendTemplateMessage(me);
			
			return new ResponseEntity<MarkEntity>(me,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////POST FINAL MARK--MEAN////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.POST,value="/makeFinal/studentId/{sId}/timetableId/{tId}")
	public ResponseEntity<?> createFinalMark(@PathVariable String sId,@PathVariable String tId) {
		try {
			if(!studentRep.existsById(Integer.parseInt(sId)))
				return new ResponseEntity<RestError>(new RestError("Student with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(tId)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.BAD_REQUEST);
			
			StudentEntity se=studentRep.findById(Integer.parseInt(sId)).get();
			TimetableEntity te=timetableRep.findById(Integer.parseInt(tId)).get();
			
			if(!te.isActive())
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id isn't active."),HttpStatus.BAD_REQUEST);
			if(!te.getTtClass().equals(se.getStClass()))
				return new ResponseEntity<RestError>(new RestError("Timetable's class doesn't match student's class."),HttpStatus.BAD_REQUEST);
			if(markRep.existsByStudentAndTimetableQualificationSubjectAndMarkType(se, te.getQualification().getSubject(), markTypeRep.findByType("zakljucna")))
				return new ResponseEntity<RestError>(new RestError("Unable to post final mark,there is already final mark."),HttpStatus.BAD_REQUEST);
			if(userRep.findById(userDao.getLoggedInID()).get().getUserRole().equals(EUserRole.ROLE_TEACHER)) {
				if(!te.getQualification().getTeacher().getId().equals((userDao.getLoggedInID())))
					return new ResponseEntity<RestError>(new RestError("Not authorised"),HttpStatus.UNAUTHORIZED);
			}
			
			MarkEntity me=new MarkEntity();
			me.setMarkValue(markDao.makeFinal(se, te));
			me.setMarkType(markTypeRep.findByType("zakljucna"));
			me.setMarkEarned(LocalDate.now());
			me.setMarkNoted(new Date());
			me.setNote("Ocena zakljucena kao aritmeticka sredina");
			me.setStudent(se);
			me.setTimetable(te);
			markRep.save(me);
			
			emailService.sendTemplateMessage(me);
			
			return new ResponseEntity<MarkEntity>(me,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////GET MARK BY ID///////////////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getMarkById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<MarkEntity>(markRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////DELETE MARK BY ID////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteMarkById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			MarkEntity me=markRep.findById(Integer.parseInt(id)).get();
			markRep.delete(me);
			return new ResponseEntity<MarkEntity>(me, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
