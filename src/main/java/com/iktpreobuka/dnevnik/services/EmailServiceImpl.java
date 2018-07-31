package com.iktpreobuka.dnevnik.services;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.iktpreobuka.dnevnik.entities.MarkEntity;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
    public JavaMailSender emailSender;

	@Override
	public void sendTemplateMessage(MarkEntity mark) throws Exception {
		
		MimeMessage mail = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        
        helper.setTo(mark.getStudent().getParent().getEmail());
        helper.setSubject("Elektronski Dnevnik - NOVA OCENA");
        
        String text = "<html><body>"+ "<table style='border:2px solid black'>"
		+ "<tr><th>Ucenik</th><th>Predmet</th><th>Nastavnik</th><th>Ocena</th><th>Tip</th><th>Datum provere</th><th>Datum upisa</th><th>Napomena</th></tr>"
		+ "<tr><td>" + mark.getStudent().getFirstName() + " " + mark.getStudent().getLastName() +"</td>"
		+ "<td>" + mark.getTimetable().getQualification().getSubject().getSubject().getTitle() + "</td>"
		+ "<td>" + mark.getTimetable().getQualification().getTeacher().getFirstName() + " " + mark.getTimetable().getQualification().getTeacher().getLastName() + "</td>"
		+ "<td>" + mark.getMarkValue().toString() + "</td>"
		+ "<td>" + mark.getMarkType().getType() + "</td>"
		+ "<td>" + mark.getMarkEarned().toString() + "</td>"
		+ "<td>" + mark.getMarkNoted().toString() + "</td>"
		+ "<td>" + mark.getNote() + "</td></tr></table></body></html>";
        
        helper.setText(text, true);
        
        emailSender.send(mail); 

	}

}
