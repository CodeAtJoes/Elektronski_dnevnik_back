package com.iktpreobuka.dnevnik.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.SubjectEntity;
import com.iktpreobuka.dnevnik.repositories.GradeRepository;
import com.iktpreobuka.dnevnik.repositories.SubjectRepository;
import com.iktpreobuka.dnevnik.repositories.SubjectTitleRepository;

@RestController
@RequestMapping(path="/register/subjects")
public class SubjectController {
	
	@Autowired
	SubjectRepository subjectRep;
	
	@Autowired
	SubjectTitleRepository subjectTitleRep;
	
	@Autowired
	GradeRepository gradeRep;
	
	//////////////////////////////////////////GET ALL///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			List<SubjectEntity> subjects = (List<SubjectEntity>) subjectRep.findAll();
			return new ResponseEntity<List<SubjectEntity>>(subjects, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////ADD NEW SUBJECTS/////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.POST,value="/createSubject/{subjectTitleId}")
	public ResponseEntity<?> createSubject(@PathVariable String subjectTitleId,@RequestParam("grades") String selGrades,@RequestParam("weekLoads") String selWeekLoads){
		try {
			if(!subjectTitleId.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Subject id must be integer."),HttpStatus.BAD_REQUEST);
			if(!selGrades.matches("[1-8]{1,8}")||!selWeekLoads.matches("[1-9]{1,8}"))
				return new ResponseEntity<RestError>(new RestError("Grades take only values from 1-8, and weekloads from 1-9."),HttpStatus.BAD_REQUEST);
			if(!subjectTitleRep.existsById(Integer.parseInt(subjectTitleId)))
				return new ResponseEntity<RestError>(new RestError("Subject title with the provided id not found."),HttpStatus.NOT_FOUND);
			if(selGrades.length()!=selWeekLoads.length())
				return new ResponseEntity<RestError>(new RestError("Number of selected grades must be the same as number of selected weekloads."),HttpStatus.BAD_REQUEST);
			List<SubjectEntity> existing=new ArrayList<SubjectEntity>();
			List<SubjectEntity> saved=new ArrayList<SubjectEntity>();
			for(int i=0; i<selGrades.length();i++) {
				SubjectEntity se=new SubjectEntity();
				se.setSubject(subjectTitleRep.findById(Integer.parseInt(subjectTitleId)).get());
				se.setGrade(gradeRep.findById(Integer.parseInt(selGrades.substring(i, i+1))).get());
				se.setWeekLoad(Integer.parseInt(selWeekLoads.substring(i, i+1)));
				if(subjectRep.existsByGradeAndSubject(se.getGrade(),se.getSubject()))
					existing.add(se);
				else {
					subjectRep.save(se);
					saved.add(se);
				} 
			}
			HashMap<String,List<SubjectEntity>> map=new HashMap<String,List<SubjectEntity>>();
			map.put("Already existing:",existing );
			map.put("Saved subjects:", saved);
			return new ResponseEntity<HashMap<String,List<SubjectEntity>>>(map,HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	////////////////////////////////////UPDATE SUBJECT/////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> updateSubjectTitle(@PathVariable String id,@RequestBody SubjectEntity subject) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Subject id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			SubjectEntity se=subjectRep.findById(Integer.parseInt(id)).get();
			if(subject.getGrade()!=null) se.setGrade(subject.getGrade());
			if(subject.getWeekLoad()!=null) se.setWeekLoad(subject.getWeekLoad());
			
			if(subjectRep.existsByGradeAndSubject(se.getGrade(),se.getSubject()))
				return new ResponseEntity<RestError>(new RestError("Updating subject creates already existin subject."),HttpStatus.BAD_REQUEST);
			return new ResponseEntity<SubjectEntity>(subjectRep.save(se), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////GET SUBJECT BY ID//////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getSubjectById(@PathVariable String id) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Subject id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<SubjectEntity>(subjectRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////DELETE SUBJECT BY ID///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN") 
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteSubjectById(@PathVariable String id) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Subject id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			SubjectEntity se=subjectRep.findById(Integer.parseInt(id)).get();
			
			if(!se.getQualified_teachers().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Subject is used as a qualification for some teacher."),HttpStatus.BAD_REQUEST);
			
			subjectRep.delete(se);
			return new ResponseEntity<SubjectEntity>(se, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
