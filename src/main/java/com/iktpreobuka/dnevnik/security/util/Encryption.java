package com.iktpreobuka.dnevnik.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encryption {
	
	public static String getPassEncoded(String pass) {
		BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.encode(pass);
		}
	public static Boolean matchPass(String pass, String encodedPass) {
		BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.matches(pass, encodedPass);
		}
		public static void main(String[] args) {
		System.out.println(getPassEncoded("nadapo123"));
		}

}
