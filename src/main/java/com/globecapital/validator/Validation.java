package com.globecapital.validator;

//import java.util.Base64;

import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidRequestKeyException;

public class Validation {
	
	

	public static String passwordValidation(String password) throws GCException {
		String passwordPattern = "^[A-Za-z0-9@#$%^&*().]{6,12}$";
		if(!matchesPattern(password, passwordPattern)) {
			throw new InvalidRequestKeyException("Invalid password!!");
		}
		return password;
	}
	
	public static String dobValidation(String dob) throws GCException {
		String dobPattern = "^[0-9]{8}$";
		if(!matchesPattern(dob, dobPattern)) {
			throw new InvalidRequestKeyException("Invalid dob!!");
		}
		return dob;
	}
	
/*	public static String base64Encoder(String password) {
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(password.getBytes());
	}*/
	
	private static boolean matchesPattern(String value, String pattern) {
		return value.matches(pattern);
	}

}
