package com.iktpreobuka.dnevnik.entities.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MarkDTO {
	
	@NotNull(message = "Mark value must be provided.")
	@Pattern(regexp= "^[1-5]$",message="Mark value must be integer between 1 and 5.")
	private String markValue;
	
	@Size(max=140, message = "Note must be up to {max} characters long.")
	private String note;
	
	@NotNull(message = "Examination date must be provided.")
	@Pattern(regexp="^(?:(?:31(-)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(-)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(-)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(-)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$",message="Date must be in dd-MM-yyyy format")
	private String markEarned;
	
	@NotNull(message = "Mark type must be provided.")
	@Pattern(regexp= "^\\d+$",message="Mark type id must be integer.")
	private String markTypeId;
	
	@NotNull(message = "Student must be provided.")
	@Pattern(regexp= "^\\d+$",message="Student id must be integer.")
	private String studentId;
	
	@NotNull(message = "Timetable must be provided.")
	@Pattern(regexp= "^\\d+$",message="Timetable id must be integer.")
	private String timetableId;

	public MarkDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMarkValue() {
		return markValue;
	}

	public void setMarkValue(String markValue) {
		this.markValue = markValue;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getMarkEarned() {
		return markEarned;
	}

	public void setMarkEarned(String markEarned) {
		this.markEarned = markEarned;
	}

	public String getMarkTypeId() {
		return markTypeId;
	}

	public void setMarkTypeId(String markTypeId) {
		this.markTypeId = markTypeId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getTimetableId() {
		return timetableId;
	}

	public void setTimetableId(String timetableId) {
		this.timetableId = timetableId;
	}
	
	

}
