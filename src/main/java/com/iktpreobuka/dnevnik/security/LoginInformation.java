package com.iktpreobuka.dnevnik.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class LoginInformation {
	
	
		public static Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		public static String username= auth.getName();
		
		
}
