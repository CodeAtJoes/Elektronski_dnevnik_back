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
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dnevnik.controllers.util.RestError;
import com.iktpreobuka.dnevnik.entities.SubjectTitleEntity;
import com.iktpreobuka.dnevnik.repositories.GradeRepository;
import com.iktpreobuka.dnevnik.repositories.SubjectRepository;
import com.iktpreobuka.dnevnik.repositories.SubjectTitleRepository;

@RestController
@RequestMapping(path = "/register/subjectTitles")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class SubjectTitleController {

	@Autowired
	SubjectTitleRepository subjectTitleRep;
	
	@Autowired
	SubjectRepository subjectRep;
	
	@Autowired
	GradeRepository gradeRep;
	
	/////////////////////////////////////GET ALL///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			List<SubjectTitleEntity> subjectNames = (List<SubjectTitleEntity>) subjectTitleRep.findAll();
			return new ResponseEntity<List<SubjectTitleEntity>>(subjectNames, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////////POST SUBJECT TITLE//////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/{subjectTitle}")
	public ResponseEntity<?> addNewSubjectTitle(@PathVariable String subjectTitle) {
		try {
			/*if(subjectTitle.isEmpty())
				return new ResponseEntity<RestError>(new RestError("Subject title must be provided."),HttpStatus.BAD_REQUEST);*/
			SubjectTitleEntity newSubject = new SubjectTitleEntity();
			for (SubjectTitleEntity ste : subjectTitleRep.findAll())
				if (ste.getTitle().equalsIgnoreCase(subjectTitle))
					return new ResponseEntity<RestError>(new RestError("Subject title already exists."),HttpStatus.BAD_REQUEST);
			newSubject.setTitle(subjectTitle);
			return new ResponseEntity<SubjectTitleEntity>(subjectTitleRep.save(newSubject), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////////UPDATE ST///////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/{subjectTitle}")
	public ResponseEntity<?> updateSubjectTitle(@PathVariable String id,@PathVariable String subjectTitle) {
		try {
			if(!id.matches("[0-9]+"))
				return new ResponseEntity<RestError>(new RestError("Subject title id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectTitleRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject title with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			SubjectTitleEntity ste=subjectTitleRep.findById(Integer.parseInt(id)).get();
			ste.setTitle(subjectTitle);
			/*for(SubjectEntity se:subjectRep.findAll())
				if(se.getSubject().getId().toString().equals(id))
					se.setSubject(ste);*/
			return new ResponseEntity<SubjectTitleEntity>(subjectTitleRep.save(ste), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////////GET ST BY ID///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getSubjectTitleById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Subject title id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectTitleRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject title with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<SubjectTitleEntity>(subjectTitleRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	///////////////////////////////////DELETE ST BY ID///////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteSubjectById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Subject title id must be integer."),HttpStatus.BAD_REQUEST);
			if(!subjectTitleRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Subject title with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			SubjectTitleEntity ste=subjectTitleRep.findById(Integer.parseInt(id)).get();
			
			if(!ste.getSubject_grades().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Subject title is used in some subjects."),HttpStatus.BAD_REQUEST);
			
			subjectTitleRep.delete(ste);
			return new ResponseEntity<SubjectTitleEntity>(ste, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
