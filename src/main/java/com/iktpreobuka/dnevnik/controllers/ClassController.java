package com.iktpreobuka.dnevnik.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.ClassEntity;
import com.iktpreobuka.dnevnik.repositories.ClassRepository;
import com.iktpreobuka.dnevnik.repositories.GradeRepository;

@RestController
@RequestMapping(path="/register/classes")
public class ClassController {
	
	@Autowired
	ClassRepository classRep;
	
	@Autowired
	GradeRepository gradeRep;
	
	///////////////////////////////////////GET ALL////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll(){
		try {
			List<ClassEntity> classes=(List<ClassEntity>) classRep.findAll();
			return new ResponseEntity<List<ClassEntity>>(classes,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////POST NEW CLASSES/////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST,value="/grade/{gradeId}/classes/{selClasses}")
	public ResponseEntity<?> addClassByGrade(@PathVariable String gradeId,@PathVariable String selClasses){
		try {	
			if(!gradeId.matches("[1-8]"))
					return new ResponseEntity<RestError>(new RestError("Grade id must be integer,1-8."),HttpStatus.BAD_REQUEST);
				if(!selClasses.matches("[1-6]{1,6}"))
					return new ResponseEntity<RestError>(new RestError("Classes take only values from 1-6."),HttpStatus.BAD_REQUEST);
				if(!gradeRep.existsById(Integer.parseInt(gradeId)))
						return new ResponseEntity<RestError>(new RestError("Grade with the provided id not found."),HttpStatus.NOT_FOUND);
				List<ClassEntity> existing=new ArrayList<ClassEntity>();
				List<ClassEntity> saved=new ArrayList<ClassEntity>();
				for(int i=0; i<selClasses.length();i++) {
					ClassEntity ce=new ClassEntity();
					ce.setGrade(gradeRep.findById(Integer.parseInt(gradeId)).get());
					ce.setIndeks(Integer.parseInt(selClasses.substring(i, i+1)));
					if(classRep.existsByGradeAndIndeks(ce.getGrade(),ce.getIndeks()))
						existing.add(ce);
					else {
						classRep.save(ce);
						saved.add(ce);
					} 
				}
				HashMap<String,List<ClassEntity>> map=new HashMap<String,List<ClassEntity>>();
				map.put("Already existing:",existing );
				map.put("Saved classess:", saved);
				return new ResponseEntity<HashMap<String,List<ClassEntity>>>(map,HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	//////////////////////////////////UPDATE CLASS/////////////////////////////////////
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/indeks/{classIndeks}")
	public ResponseEntity<?> updateClassIndeks(@PathVariable String id,@PathVariable String classIndeks) {
		try {
			if(!classIndeks.matches("[1-6]"))
				return new ResponseEntity<RestError>(new RestError("Classes take only values from 1-6."),HttpStatus.BAD_REQUEST);
			if(!classRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Class with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			ClassEntity ce=classRep.findById(Integer.parseInt(id)).get();
			ce.setIndeks(Integer.parseInt(classIndeks));
			return new ResponseEntity<ClassEntity>(classRep.save(ce), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////GET CLASS BY ID///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getClassById(@PathVariable String id) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Id is not valid."),HttpStatus.BAD_REQUEST);
			if(!classRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Class with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<ClassEntity>(classRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////DELETE CLASS BY ID//////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteClassById(@PathVariable String id) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Id is not valid."),HttpStatus.BAD_REQUEST);
			if(!classRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Class with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			ClassEntity ce=classRep.findById(Integer.parseInt(id)).get();
			if(!ce.getTimetable_qualifications().isEmpty()||!ce.getStudents().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Class is involved in timetable and/or has students."),HttpStatus.BAD_REQUEST);
			classRep.delete(ce);
			return new ResponseEntity<ClassEntity>(ce, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
