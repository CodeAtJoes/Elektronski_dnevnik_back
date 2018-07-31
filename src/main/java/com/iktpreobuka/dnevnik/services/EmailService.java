package com.iktpreobuka.dnevnik.services;

import com.iktpreobuka.dnevnik.entities.MarkEntity;

public interface EmailService {
	
	void sendTemplateMessage(MarkEntity mark) throws Exception;

}
