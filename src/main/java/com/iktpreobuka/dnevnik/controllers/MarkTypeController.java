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
import com.iktpreobuka.dnevnik.entities.MarkTypeEntity;
import com.iktpreobuka.dnevnik.repositories.MarkTypeRepository;

@RestController
@RequestMapping(path="/register/markTypes")
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
public class MarkTypeController {
	
	@Autowired
	MarkTypeRepository markTypeRep;
	
	////////////////////////////////////GET ALL/////////////////////////////////////////////////
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			List<MarkTypeEntity> markTypes = (List<MarkTypeEntity>) markTypeRep.findAll();
			return new ResponseEntity<List<MarkTypeEntity>>(markTypes, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////ADD NEW MARK TYPE/////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/{markType}")
	public ResponseEntity<?> addNewMarkType(@PathVariable String markType) {
		try {
			MarkTypeEntity newMarkType = new MarkTypeEntity();
			for (MarkTypeEntity mte : markTypeRep.findAll())
				if (mte.getType().equalsIgnoreCase(markType))
					return new ResponseEntity<RestError>(new RestError("Mark type already exists."),HttpStatus.BAD_REQUEST);
			newMarkType.setType(markType);
			return new ResponseEntity<MarkTypeEntity>(markTypeRep.save(newMarkType), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//////////////////////////////////////UPDATE MT////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/{markType}")
	public ResponseEntity<?> updateMarkType(@PathVariable String id,@PathVariable String markType) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark type id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markTypeRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark type with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			MarkTypeEntity mte=markTypeRep.findById(Integer.parseInt(id)).get();
			mte.setType(markType);
			return new ResponseEntity<MarkTypeEntity>(markTypeRep.save(mte), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	////////////////////////////////////GET MT BY ID////////////////////////////////////////////////////
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<?> getMarkTypeById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark type id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markTypeRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("Mark type with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			return new ResponseEntity<MarkTypeEntity>(markTypeRep.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/////////////////////////////////////DELETE MT BY ID//////////////////////////////////////////////
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteMarkTypeById(@PathVariable String id) {
		try {
			if(!id.matches("\\d+"))
				return new ResponseEntity<RestError>(new RestError("Mark type id must be integer."),HttpStatus.BAD_REQUEST);
			if(!markTypeRep.existsById(Integer.parseInt(id)))
				return new ResponseEntity<RestError>(new RestError("MarkType with the provided id doesn't exist."),HttpStatus.NOT_FOUND);
			MarkTypeEntity mte=markTypeRep.findById(Integer.parseInt(id)).get();
			
			if(!mte.getMarks().isEmpty())
				return new ResponseEntity<RestError>(new RestError("Mark type is used in some marks."),HttpStatus.BAD_REQUEST);
			
			markTypeRep.delete(mte);
			return new ResponseEntity<MarkTypeEntity>(mte, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError("Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
