package com.iktpreobuka.dnevnik.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.ClassEntity;
import com.iktpreobuka.dnevnik.entities.QualifiedEntity;
import com.iktpreobuka.dnevnik.entities.TimetableEntity;
import com.iktpreobuka.dnevnik.repositories.ClassRepository;
import com.iktpreobuka.dnevnik.repositories.QualifiedRepository;
import com.iktpreobuka.dnevnik.repositories.TimetableRepository;

@RestController
@RequestMapping(path="/register/timetables")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*") 
public class TimetableController {
	
	@Autowired
	TimetableRepository timetableRep;
	
	@Autowired
	ClassRepository classRep;
	
	@Autowired
	QualifiedRepository qualifiedRep;
	
	///////////////////////GET ALL--SEARCH//////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			List<TimetableEntity> timetables = (List<TimetableEntity>) timetableRep.findAll();
			return new ResponseEntity<List<TimetableEntity>> (timetables, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////POST TIMETABLE///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> crateTimetable(@RequestParam(name="classId") String classId,@RequestParam(name="qualifiedId") String qualifiedId){
		try {
			if(!classId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Class id must be integer."),HttpStatus.BAD_REQUEST);
			if(!qualifiedId.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Qualified id must be integer."),HttpStatus.BAD_REQUEST);
			if(!classRep.existsById(Integer.parseInt(classId))) 
				return new ResponseEntity<RestError>(new RestError("Class with the provided id not found"),HttpStatus.NOT_FOUND);
			if(!qualifiedRep.existsById(Integer.parseInt(qualifiedId)))
				return new ResponseEntity<RestError>(new RestError("Qualified with the provided id not found"),HttpStatus.NOT_FOUND);
			
			ClassEntity ce = classRep.findById(Integer.parseInt(classId)).get();
			QualifiedEntity qe=qualifiedRep.findById(Integer.parseInt(qualifiedId)).get();
			
			if(timetableRep.existsByTtClassAndQualification(ce,qe))
				return new ResponseEntity<RestError>(new RestError("Timetable for the selected class and qualification already exists."),HttpStatus.BAD_REQUEST);
			
			if(!ce.getGrade().equals(qe.getSubject().getGrade()))
				return new ResponseEntity<RestError>(new RestError("Selected qualification subject isn't in the selected class grade curriculum."),HttpStatus.BAD_REQUEST);
			
			if(timetableRep.existsByTtClassAndQualificationSubjectAndActive(ce, qe.getSubject(), true))
				return new ResponseEntity<RestError>(new RestError("There is already active timetable for the selected class and qualification subject."),HttpStatus.BAD_REQUEST);
			
			TimetableEntity te=new TimetableEntity();
			te.setTtClass(ce);
			te.setQualification(qe);
			te.setActive(true);
			
			return new ResponseEntity<TimetableEntity>(timetableRep.save(te),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////////DEACTIVATE TT////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/deactivate")
	public ResponseEntity<?> deactivateTimetable(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			
			TimetableEntity te=timetableRep.findById(Integer.parseInt(id)).get();
			te.setActive(false);
			
			return new ResponseEntity<TimetableEntity>(timetableRep.save(te), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////ACTIVATE TT////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/activate")
	public ResponseEntity<?> activateTimetable(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			
			TimetableEntity te=timetableRep.findById(Integer.parseInt(id)).get();
			if(timetableRep.existsByTtClassAndQualificationSubjectAndActive(te.getTtClass(), te.getQualification().getSubject(), true))
				return new ResponseEntity<RestError>(new RestError("There is already active timetable for the same class and qualification-subject, that first must be deactivated."),HttpStatus.BAD_REQUEST);
			
			te.setActive(true);
			
			return new ResponseEntity<TimetableEntity>(timetableRep.save(te), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////GET TT BY ID////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getTimetableById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<TimetableEntity>(timetableRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////////DELETE TT BY ID////////////////////////////////////////////
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteTimetableById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Id must be integer."),HttpStatus.BAD_REQUEST);
			if(!timetableRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Timetable with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			
			TimetableEntity te=timetableRep.findById(Integer.parseInt(id)).get();
			
			if(!te.getMarks().isEmpty()||te.isActive())
				return new ResponseEntity<RestError>(new RestError("Timetable cannot be deleted."),HttpStatus.BAD_REQUEST);
			timetableRep.delete(te);
			return new ResponseEntity<TimetableEntity>(te, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
